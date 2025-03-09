package com.tshs.moduleapp.account.data


import org.mindrot.jbcrypt.BCrypt

class PasswordHasher {

    companion object{
        fun hashPassword(password: String): String{
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }
        fun checkPassword(password: String, hashedPassword: String): Boolean{
            return BCrypt.checkpw(password, hashedPassword)
        }

    }

}