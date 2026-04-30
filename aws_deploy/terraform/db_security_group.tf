resource "aws_security_group" "db" {
 vpc_id = aws_vpc.main_vpc.id

 ingress {
 from_port = 5432
 to_port = 5432
 protocol = "tcp"
 security_groups = [aws_security_group.app.id]
 }

 tags = {
    Name = "db security group"
  }
}