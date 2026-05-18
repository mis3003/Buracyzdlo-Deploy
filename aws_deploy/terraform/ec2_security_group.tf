resource "aws_security_group" "app" {
 vpc_id = aws_vpc.main_vpc.id

 ingress {
 from_port = 22
 to_port = 22
 protocol = "tcp"
 cidr_blocks = ["0.0.0.0/0"] 
 }
 ingress {
 from_port = 30000
 to_port = 30000
 protocol = "tcp"
 cidr_blocks = ["0.0.0.0/0"] 
 }


 egress {
 from_port = 0
 to_port = 0
 protocol = "-1" 
 cidr_blocks = ["0.0.0.0/0"]
 }

 tags = {
    Name = "ec2 security group"
  }
}