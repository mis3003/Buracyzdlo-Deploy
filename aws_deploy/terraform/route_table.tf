resource "aws_route_table" "rout_table" {
  vpc_id = aws_vpc.main_vpc.id

  
route {
  cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.gateway.id
}

  tags = {
    Name = "example"
  }
}