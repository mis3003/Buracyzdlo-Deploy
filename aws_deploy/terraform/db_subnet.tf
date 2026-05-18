resource "aws_subnet" "db_subnet" {
    count = 2 

  vpc_id     = aws_vpc.main_vpc.id
  cidr_block = "10.0.${count.index + 2}.0/24"
  availability_zone = "eu-north-1${count.index == 0 ? "a" : "b"}"

  tags = {
    Name = "rds ${count.index}"
  }
}

