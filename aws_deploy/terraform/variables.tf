# variables.tf
variable "db_password" {
  description = "Hasło do bazy danych"
  type        = string
  sensitive   = true  # Terraform nie wypisze jej w logach
}

variable "db_username" {
  description = "username do bazy danych"
  type        = string
  sensitive   = true  # Terraform nie wypisze jej w logach
}