resource "aws_api_gateway_rest_api" "api" {
  name = "flatjaeger-api"
  body = file("../src/main/resources/api.yml")
}
