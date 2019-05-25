resource "aws_s3_bucket" "flatjaeger" {
  bucket = "flatjaeger"
  acl    = "private"
}
