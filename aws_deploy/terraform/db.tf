resource "aws_db_instance" "db_instance" {
      allocated_storage    = 10
  db_name              = "postgres"
  engine               = "postgres"
  engine_version       = "16"
  instance_class       = "db.t4g.micro"
  password = var.db_password
  username = var.db_username
  skip_final_snapshot  = true 
  vpc_security_group_ids = [aws_security_group.db.id]
  db_subnet_group_name =aws_db_subnet_group.db_subnet_group.name
}