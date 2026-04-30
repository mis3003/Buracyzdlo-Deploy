resource "aws_route_table_association" "a" {
  subnet_id      = aws_subnet.ec2_subnet.id
  route_table_id = aws_route_table.rout_table.id
}