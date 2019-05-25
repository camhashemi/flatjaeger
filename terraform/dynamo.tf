resource "aws_dynamodb_table" "requests" {
  name           = "fj_requests"
  billing_mode   = "PROVISIONED"
  read_capacity  = 1
  write_capacity = 1

  hash_key = "UserId"

  attribute {
    name = "UserId"
    type = "S"
  }

  server_side_encryption {
    enabled = "false"
  }
}
