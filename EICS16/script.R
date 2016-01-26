# init data
library(plyr) # to rename headers

dataXP <- read.table("res.txt", head=FALSE, sep=";")
names <- read.table("resName.txt", head=FALSE, sep=";")

dataXP <-rename(dataXP, c("V1"="project", "V2"="file", "V3"="startLine", "V4"="endLine", "V5"="cmds", "V6"="bugs", "V7"="commits"))
names <- rename(names, c("V1"="project", "V2"="file", "V3"="startLine", "V4"="listName"))

dataXP$commits <- dataXP$commits -1

dataXP$nbLines <- (dataXP$endLine - dataXP$startLine + 1)
dataXP$comPerLines <- dataXP$commits / dataXP$nbLines
dataXP$bugPerLines <- dataXP$bugs / dataXP$nbLines
dataXP$linesPerCmd <- dataXP$nbLines / dataXP$cmds
dataXP$listName <- names$listName

dataXP <- dataXP[(dataXP$listName!="eventDispatched") & (dataXP$listName!="windowLostFocus") & (dataXP$listName!="focusGained") & (dataXP$listName!="focusLost") & (dataXP$listName!="ancestorAdded") & (dataXP$listName!="hierarchyChanged") & (dataXP$listName!="componentHidden") & (dataXP$listName!="ancestorRemoved"),]
dataXP <- dataXP[dataXP$project!="freemind-code",]

# first step

cmd1 <- dataXP[dataXP$cmds==1,]
cmd2 <- dataXP[dataXP$cmds==2,]
cmd3 <- dataXP[dataXP$cmds==3,]
cmdPlus <- dataXP[dataXP$cmds>3,]

boxplot(cmd1$comPerLines, cmd2$comPerLines, cmd3$comPerLines, cmdPlus$comPerLines, outline=TRUE,
        names = c("1", "2", "3", "3+"), ylab="Commits per LoC", xlab="Commmands per GUI listener (#)")

boxplot(cmd1$bugPerLines, cmd2$bugPerLines, cmd3$comPerLines, cmdPlus$bugPerLines, outline=TRUE,
        names = c("1", "2", "3", "3+"), ylab="Fault fixes per LoC", xlab="Commmands per GUI listener (#)")

# removing FIX outliers

boxplot.stats(cmd1$bugPerLines)
cmd1minout <- cmd1[(cmd1$bugPerLines==0.0), ] # 0

boxplot.stats(cmd2$bugPerLines)
cmd2minout <- cmd2[(cmd2$bugPerLines<=0.10000001), ]
mean(cmd2minout$bugPerLines) # 0.01231535

boxplot.stats(cmd3$bugPerLines)
cmd3minout <- cmd3[(cmd3$bugPerLines<=0.1224499), ]
mean(cmd3minout$bugPerLines) # 0.01905019

boxplot.stats(cmdPlus$bugPerLines)
cmdplusminout <- cmdPlus[(cmdPlus$bugPerLines<=0.14285715), ]
mean(cmdplusminout$bugPerLines) # 0.02824967


# removing COMMIT outliers

boxplot.stats(cmd1$comPerLines)
cmd1COMminout <- cmd1[(cmd1$comPerLines<=0.4000001), ] 
mean(cmd1COMminout$comPerLines) # 0.07502711

boxplot.stats(cmd2$comPerLines)
cmd2COMminout <- cmd2[(cmd2$comPerLines<=0.4000001), ]
mean(cmd2COMminout$comPerLines) # 0.07676761

boxplot.stats(cmd3$comPerLines)
cmd3COMminout <- cmd3[(cmd3$comPerLines<=0.38461539), ]
mean(cmd3COMminout$comPerLines) # 0.08491857

boxplot.stats(cmdPlus$comPerLines)
cmdplusCOMminout <- cmdPlus[(cmdPlus$comPerLines<=0.25301206), ]
mean(cmdplusCOMminout$comPerLines) # 0.05764453

###

boxplot(cmd1minout$bugPerLines, cmd2minout$bugPerLines, cmd3minout$bugPerLines, cmdplusminout$bugPerLines, outline=FALSE,
        names = c("1", "2", "3", "3+"), ylab="Fault fixes per LoC", xlab="Commmands per GUI listener (#)")

boxplot(cmd1COMminout$comPerLines, cmd2COMminout$comPerLines, cmd3COMminout$comPerLines, cmdplusCOMminout$comPerLines, outline=FALSE,
        names = c("1", "2", "3", "3+"), ylab="Commits per LoC", xlab="Commmands per GUI listener (#)")

dataXP2 <- rbind(cmd1minout, cmd2minout, cmd3minout, cmdplusminout)
dataXP3 <- rbind(cmd1COMminout, cmd2COMminout, cmd3COMminout, cmdplusCOMminout)

plot(dataXP2$cmds, dataXP2$bugPerLines)
lines(lowess(dataXP2$cmds, dataXP2$bugPerLines), col="blue") # monotonic relationship

plot(dataXP3$cmds, dataXP3$comPerLines)
lines(lowess(dataXP3$cmds, dataXP3$comPerLines), col="blue") # monotonic relationship

cor.test(dataXP2$cmds, dataXP2$bugPerLines, method = "spearman") # 0.4438693 (2.2e-16)
cor.test(dataXP3$cmds, dataXP3$comPerLines, method = "spearman") # 0.05699881 (0.1115)

### average size faulty listeners
mean(dataXP[dataXP$cmds>=3, ]$nbLines) # 42
sumLoCs <- sum(dataXP$nbLines) # 19934
sumBlobLoCs <- sum(dataXP[dataXP$cmds>=3, ]$nbLines) # 7647
# Jabref, argouml, ganttproject, flyingsaucer, FreeCol, RSyntaxTextArea, sikuli, freeplane, cids-navigator, cids-server, cismet-gui-commons, DJ-Native-Swing, pentaho-reporting.  
sizeJavaApps <- 93023+133515+62193+57765+121538+117234+36136+170028+53506+38331+25671+34395+471645 # 1414980, Java code LoCs of the studied apps
sizeJavaApps/13 # 108844.6, average Java code size
sumLoCs*100/sizeJavaApps # 1.327086 of the code is GUI listeners
sumBlobLoCs*100/sizeJavaApps #0.5404317 of the code are Blobs
182*100/858 # 21.21212 of GUI listeners are Blobs

