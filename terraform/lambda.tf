provider "aws" {
  region = "eu-central-1"
}

resource "aws_lambda_function" "jaeger" {
  function_name = "jaeger"

  s3_bucket = "flatjaeger"
  s3_key    = "jaeger-0.0.1-all.jar"

  handler = "flathunter.lambda.EventHandler::handleRequest"
  runtime = "java11"

  role = aws_iam_role.lambda_exec.arn
}

resource "aws_iam_role" "lambda_exec" {
  name = "jaeger"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
     {
       "Action": "sts:AssumeRole",
       "Principal": {
         "Service": "lambda.amazonaws.com"
       },
       "Effect": "Allow",
       "Sid": ""
     }
  ]
}
  EOF
}

