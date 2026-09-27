<script setup>
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loginForm = ref({
  userId: '',
  password: ''
})

const handleLogin = () => {
    const Login=async()=>{
        try{
            const res=await axios.post('/api/login',{
                userId:loginForm.value.userId,
                password:loginForm.value.password
            }) 
            ElMessage.success('登录成功')
            localStorage.setItem('token',res.data)
            localStorage.setItem('userId',loginForm.value.userId)
            if(res.status===202)localStorage.setItem('isA',true)
            else localStorage.setItem('isA',false)
            router.push('/list')
        }catch(err){
            if(err.response?.status===401)
            {
                ElMessage.error('用户或密码输入错误')
            }else{
                ElMessage.error('网络错误')
            }
        }finally{
            loginForm.value.userId=''
            loginForm.value.password=''
        }
    }
    Login()
}
</script>

<template>
    <div class="login-container">
    <h1 class="title">我们追求的,不是一时的奖牌,而是学生一生的能力与自信</h1>
    <el-card shadow="hover" class="login-card">
      <el-form :model="loginForm" label-width="0px">
        <el-form-item>
          <el-input
            v-model="loginForm.userId"
            placeholder="haue_学号"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.title{
    margin: 120px auto 0 auto;
    text-align: center;
}
.login-container {
  height: 80vh;
  background-color: white;
}
.login-card {
  width: 340px;
  margin: 120px auto 0 auto;
}
.login-btn {
  width: 100%;
}
</style>