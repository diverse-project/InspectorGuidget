# init data
# setwd('TBD/statistics/data')
library(plyr) # to rename headers
library(lsr) 

dataArgoUML <- read.table("dataargouml.csv", head=FALSE, sep=";")
dataFreecol <- read.table("datafreecol-git.csv", head=FALSE, sep=";")
dataJabRef <- read.table("datajabref.csv", head=FALSE, sep=";")
dataEclipse <- read.table("dataeclipse.platform.ui.csv", head=FALSE, sep=";")

dataXP <- rbind(dataArgoUML, dataJabRef, dataFreecol, dataEclipse)
dataXP <- rename(dataXP, c("V1"="project", "V2"="file", "V3"="startLine", "V4"="endLine", 
                           "V5"="nblines", "V6"="cmds", "V7"="bugs", "V8"="commits"))

# Creation of the bar plot that shows the distribution of the listeners according to their number of UI commands

svg("nbCmds.svg")
bp <- barplot(table(data4Q$cmds), ylab="", xlab="", col="lightblue", mgp=c(2,1,-0.8), ylim=c(0,220))
title(ylab="UI listeners (#)", line=1.4)
title(xlab="Commands per listener (#)", line=1.3)
text(bp, y = table(data4Q$cmds), label = table(data4Q$cmds), pos = 3)
dev.off()

#####

# Removing the initial commit
dataXP$commits <- dataXP$commits -1

# Errors to remove

dataXP <- dataXP[dataXP$cmds<=dataXP$nblines,]

## Quantiles

quantile(dataXP$nblines)
# 0%  25%  50%  75% 100% 
# 1    2    5   10   74

data4Q <- dataXP[dataXP$nblines>9,]

cmd1_4Q <- data4Q[data4Q$cmds==1,]
cmd2_4Q <- data4Q[data4Q$cmds==2,]
cmdPlus_4Q <- data4Q[data4Q$cmds>2,]

svg("bugsCmds.svg")
boxplot(cmd1_4Q$bugs, cmd2_4Q$bugs, cmdPlus_4Q$bugs, outline=FALSE, names = c("1", "2", "3+"), ylab="", xlab="")
mtext("Fault fixes", side=2, line=2)
mtext("Commmands per GUI listener (#)", side=1, line=2)
dev.off()


median(cmd1_4Q$bugs)
median(cmd2_4Q$bugs)
median(cmdPlus_4Q$bugs)

mean(cmd1_4Q$bugs)
mean(cmd2_4Q$bugs) 
mean(cmdPlus_4Q$bugs)

# effect size of means
cohensD(cmd1_4Q$bugs, cmd2_4Q$bugs)
cohensD(cmd2_4Q$bugs, cmdPlus_4Q$bugs) 
cohensD(cmd1_4Q$bugs, cmdPlus_4Q$bugs) 

shapiro.test(cmd1_4Q$bugs)
shapiro.test(cmd2_4Q$bugs)
shapiro.test(cmdPlus_4Q$bugs) 

wilcox.test(cmd1_4Q$bugs, cmd2_4Q$bugs)
wilcox.test(cmd1_4Q$bugs, cmdPlus_4Q$bugs)
wilcox.test(cmd2_4Q$bugs, cmdPlus_4Q$bugs)

boxplot(cmd1_3Q$commits, cmd2_3Q$commits, cmdPlus_3Q$commits, outline=FALSE, names = c("1", "2", "3+"), ylab="", xlab="")

median(cmd1_3Q$commits)
median(cmd2_3Q$commits)
median(cmdPlus_3Q$commits)

mean(cmd1_3Q$commits)
mean(cmd2_3Q$commits)
mean(cmdPlus_3Q$commits)

shapiro.test(cmd1_3Q$commits)
shapiro.test(cmd2_3Q$commits)
shapiro.test(cmdPlus_3Q$commits)

wilcox.test(cmd1_3Q$commits, cmd2_3Q$commits)
t.test(cmd1_3Q$commits, cmdPlus_3Q$commits)



svg("commitsCmds.svg")
boxplot(cmd1_4Q$commits, cmd2_4Q$commits, cmdPlus_4Q$commits, outline=FALSE, names = c("1", "2", "3+"), ylab="", xlab="")
mtext("Commits", side=2, line=2)
mtext("Commmands per GUI listener (#)", side=1, line=2)
dev.off()


median(cmd1_4Q$commits)
median(cmd2_4Q$commits)
median(cmdPlus_4Q$commits)

mean(cmd1_4Q$commits)
mean(cmd2_4Q$commits) 
mean(cmdPlus_4Q$commits)

# effect size of means
cohensD(cmd1_4Q$commits, cmd2_4Q$commits) 
cohensD(cmd2_4Q$commits, cmdPlus_4Q$commits)
cohensD(cmd1_4Q$commits, cmdPlus_4Q$commits)

shapiro.test(cmd1_4Q$commits)
shapiro.test(cmd2_4Q$commits)
shapiro.test(cmdPlus_4Q$commits)

wilcox.test(cmd1_4Q$commits, cmd2_4Q$commits)
wilcox.test(cmd1_4Q$commits, cmdPlus_4Q$commits)
wilcox.test(cmd2_4Q$commits, cmdPlus_4Q$commits)

# Showing the relationships

plot(data4Q$cmds, data4Q$bugs, xlab="Commands per listeners", ylab="Bugs")
lines(lowess(data4Q$cmds, data4Q$bugs), col="blue") # linear relationship

plot(data4Q$cmds, data4Q$commits)
lines(lowess(data4Q$commits, data4Q$commits), col="blue") # linear relationship

# Computing the correlation

cor.test(data4Q$cmds, data4Q$bugs, method = "pearson")
cor.test(data4Q$cmds, data4Q$commits, method = "pearson")

