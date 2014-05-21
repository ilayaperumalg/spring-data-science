options(warn=-1) #suppress warning messages
suppressPackageStartupMessages(library('pmml')) #suppress loading library message for dependend libraries
require(pmml,quietly=TRUE) #suppress loading library message


#Splitts the given dataset in a training dataset(trainset) and test dataset(testset)
splitDataFrame <- function(dataframe, seed = NULL, n = trainSize) {
  
  if (!is.null(seed)){
    set.seed(seed)
  }
  
  index <- 1:nrow(dataframe)
  trainindex <- sample(index, n)
  trainset <- dataframe[trainindex, ]
  testset <- dataframe[-trainindex, ]
  
  list(trainset = trainset, testset = testset)
}

loadData <- function(){
  #load the iris data
  data(iris)
  
  #Split the iris dataset into test and train sets
  dataSets <- splitDataFrame(iris, seed = 1337, n= round(0.95 * nrow(iris)))
}

buildModel <- function(dataSets){
  model <- lm(Petal.Width ~ Petal.Length, data=dataSets$trainset)
}

exportPmml <- function(model, modelName=NULL, externalId=NULL){
  pmml.lm(model,model.name=paste(modelName,externalId,sep = ";"))
}

# called from outside
rebuildModel <- function(modelName=NULL, externalId=NULL){
  dataSet = loadData()
  model = buildModel(dataSet)
  pmmlSource = exportPmml(model, modelName, externalId)
  cat(toString(pmmlSource))
}

# called from outside
benchModel <- function(id){
  
  dataSets = loadData()
  model = buildModel(dataSets)
  summary(model)
}