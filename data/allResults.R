# data <- read.csv("concatAllStatistics.data", head=TRUE, sep=";")

# library(stringr)

listeners <- data[data$isListener == "yes" & data$Size>0,]
notListeners <- data[data$isListener == "no" & data$Size>0,]

# CC
t.test(listeners$Complexity, notListeners$Complexity)

# size
t.test(listeners$Size, notListeners$Size)

# number of LoC
sum(data$Size)

# number of listener methods
sum(listeners$Size)
sum(notListeners$Size)

# bins=seq(0,400,by=1)
# hist(ccListeners, ylab="# listeners", xlab="CC", col="lightblue", breaks=bins, xlim=c(0,30),
#      freq=FALSE, ylim=c(0, 0.15)) #ylim=c(0, 500), 
# bins=seq(0,1900,by=1)

#hist(ccNotListeners, ylab = "# methods", xlab = "CC", col="lightblue", breaks=bins, xlim=c(0,40),  ylim=c(0, 500))

# boxplot(ccListeners, xlab="Listeners", ylab="CC", log = "y", outline=FALSE) #outline=FALSE
# boxplot(ccNotListeners, xlab="Methods", ylab="CC", log = "y", outline=FALSE)

# result <- shapiro.test(ccListeners)
# result

#Kolmogorov-Smirnov ks.test() to compare the distribution of 2 samples

#boxplot(data, names=c("Notes"), ylab="Points", col="white")
#hist(data)



#--------------------

condListener <- listeners$If #+ reducedListener$Switch + reducedListener$Case #+ reducedListener$Conditional
condNotListener <- notListeners$If #+ reducedNotListener$Switch + reducedNotListener$Case# + reducedNotListener$Conditional

# Test on conditional statements
t.test(condListener,condNotListener)


switchListener <- listeners$Switch + listeners$Case #+ reducedListener$Conditional
switchNotListener <- notListeners$Switch + notListeners$Case# + reducedNotListener$Conditional

# Test on switch statements
t.test(switchListener,switchNotListener)


loopListener <- listeners$For + listeners$ForEach + listeners$While + listeners$DoWhile
loopNotListener <- notListeners$For + notListeners$ForEach + notListeners$While + notListeners$DoWhile

# Test on loop statements
t.test(loopListener,loopNotListener)

# Test on return statements
t.test(listeners$Return, notListeners$Return)


# The name of the listeners having a high CC
# names <- str_split(listeners[listeners$Complexity>5,c("Nom")], "\\.")
nameLists <- sub("^.*\\.(.*)$", "\\1", listeners[listeners$If>2,c("Nom")]) 

# unique(names)
length(nameLists)
tabNames <- table(names)
tabNames # counts redundency.

tab <- table(listeners$If)


plot(as.numeric(names(tab)),as.numeric(tab), xlab = "# if", ylab="# listeners", log="y")
grid()

# original.parameters<-par()
# par(xaxt="n")
# lablist <- unique(names)

# plot(as.numeric(names(tabNames)),as.character.default(tabNames), xlab = "# if", ylab="# listeners", log="y")
# plot(table(names), xlab = "", ylab="#",  ylim=c(50, 100), col="lightblue")
# axis(1, at=seq(1, length(lablist), by=1), labels = FALSE)
# text(seq(1, length(lablist), by=1), par("usr")[3]+2, offset = 0, labels = lablist, srt = 90, cex = 1, pos = 2, xpd = TRUE)

# bigListeners <- which(ifListeners>2)

# bins=seq(0,200,by=1)
# hist(ifListeners, ylab = "# methods", xlab = "# IF", col="lightblue", breaks=bins, xlim=c(4,40),  ylim=c(0, 100))#, 

# boxplot(ifListeners, xlab="Listeners", ylab="CC", outline=FALSE)


