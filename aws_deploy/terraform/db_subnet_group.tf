resource "aws_db_subnet_group" "db_subnet_group" {
    
  name       = "main"
  subnet_ids = aws_subnet.db_subnet[*].id

  tags = {
    Name = "My DB subnet group"
  }
}