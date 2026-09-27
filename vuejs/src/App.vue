<script setup>
import router from '@/utils/router'
import { onMounted, onUnmounted, ref } from 'vue'
import request from './utils/request'
import { ElMessage } from 'element-plus'

const needShowLoginBtn = ref(true)
const nowUser=ref('')
const showContext=ref(false)

const loginout=()=>{
  localStorage.removeItem('token')
  localStorage.removeItem('isA')
  localStorage.removeItem('gameId')
  localStorage.removeItem('userId')
  localStorage.removeItem('isA')
  router.push('/')
}

const goP=()=>{
  const id=localStorage.getItem("gameId")
  if(id)router.push('/problem/'+id)
  else if(localStorage.getItem('userId'))ElMessage.error("请先选择一个比赛")
  else ElMessage.error("请先登录")
}

const goRank=()=>{
  const id=localStorage.getItem("gameId")
  if(id)router.push('/rank/'+id)
  else if(localStorage.getItem('userId'))ElMessage.error("请先选择一个比赛")
  else ElMessage.error("请先登录")
}

const goStatus=()=>{
  const id=localStorage.getItem("gameId")
  if(id)router.push('/status')
  else if(localStorage.getItem('userId'))ElMessage.error("请先选择一个比赛")
  else ElMessage.error("请先登录")
}

const readUserState = () => {
  try {
    const token = localStorage.getItem('token')
    needShowLoginBtn.value = !token
    if (!needShowLoginBtn.value) {
      nowUser.value = localStorage.getItem('userId') || '未知用户'
    } else {
      nowUser.value = ''
    }
  } catch (err) {
    needShowLoginBtn.value = true
    nowUser.value = ''
  }
}

const Ad=()=>{
  if(localStorage.getItem('isA')==null)return false
  return localStorage.getItem('isA')=='true'
}

let stopAfterEach = null
onMounted(() => {
  readUserState()
  stopAfterEach = router.afterEach(() => {
    readUserState()
    showContext.value = false
  })
})

onUnmounted(() => {
  if (stopAfterEach) stopAfterEach()
})
</script>

<template>
  <div class="layout">
    <el-container>
      <el-header>
        <span @click="router.push('/list')" style="margin-left: 100px;">Competitions</span>
        <span @click="goStatus">Status</span>
        <span @click="goRank">Ranking</span>
        <span @click="goP">Problems</span>
        <!-- <span v-if="Ad()" @click="router.push('/mg')">Management</span> -->
        <span v-if="!needShowLoginBtn"
        @click="loginout"
        @mouseover="showContext=true"
        @mouseout="showContext=false"
        style="margin-left: auto;font-size: 20px;margin-right: 100px;">{{ showContext?'退出登录?':nowUser }}</span>
      </el-header>

      <el-main>
        <router-view :key="$route.fullPath"></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
 
:deep(.el-header){
    display: flex;
    align-items: center;
    border-bottom: 1px solid rgba(0,0,0,0.2);
    background-color: rgb(213, 254, 251);
    gap: 100px;
    margin: 0;
    padding: 0;
}

:deep(.el-header>span){
    font-weight: 700;
    font-size: 20px;
    user-select: none;
}

:deep(.el-main){
    margin: 0;
    padding: 0;
}

:deep(.el-footer){
    border-top:1px solid rgba(0,0,0,0.2);
    margin: 0;
    padding: 0;
}

.block_direct{
    margin: auto 0;
}

.layout{
    height: 100vh;
}

:deep(.el-container){
  background-color: white;
}

.login-chosen{
  display: flex;

}
</style>

<style>
html,body{
    margin: 0;
    padding: 0;
    height:100%;
    font-family: 'STXihei'
}
</style>