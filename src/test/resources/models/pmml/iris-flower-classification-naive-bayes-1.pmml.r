#Helper function to split the given dataset into a dataset used for training (trainset) and (testset) used for evaulation.
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

#Load the IRIS example dataset
data(iris)

#We want to use 95% of the IRIS data as training data and 5% as test data for evaluation.
datasets <- splitDataFrame(iris, seed = 1337, n= round(0.95 * nrow(iris)))

#Load library with the naive bayes algorithm support.
require(e1071)

#Create a naive Bayes classifier to predict iris flower species (iris[,5])
#from [,1:4] = Sepal.Length Sepal.Width Petal.Length Petal.Width
model <- naiveBayes(datasets$trainset[,c('Sepal.Length', 'Sepal.Width', 'Petal.Length', 'Petal.Width')], datasets$trainset$Species)

require(caret)
pred <- predict(model, datasets$testset[,c('Sepal.Length', 'Sepal.Width', 'Petal.Length', 'Petal.Width')])
confusionMatrix(table(pred, datasets$testset$Species))

#Load library with PMML export support.
require(pmml)

#Convert the given model into a PMML model definition
pmmlDefinition = pmml.naiveBayes(model,model.name="iris-flower-classification-naive-bayes-1", predictedField='Species')

#Print the PMML definition to stdout
cat(toString(pmmlDefinition))