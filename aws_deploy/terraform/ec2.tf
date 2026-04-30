resource "aws_instance" "app_instance"{
ami ="ami-05d62b9bc5a6ca605"
instance_type = "t3.micro"

tags = {
    Name = "app_instance"
  }

 vpc_security_group_ids  = [aws_security_group.app.id]
 subnet_id= aws_subnet.ec2_subnet.id
 key_name = "EC2_do_nauki"
}
