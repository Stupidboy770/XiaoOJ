<script setup>
import request from '@/utils/request';
import router from '@/utils/router';
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
//时间差
function getDiffStr(startStr, endStr) {
  if(!startStr || !endStr) return '-'
  const start = new Date(startStr).getTime()
  const end = new Date(endStr).getTime()
  const diffMs = end - start
  if(diffMs < 0) return '时间异常'
  const totalSec = Math.floor(diffMs / 1000)
  const h = Math.floor(totalSec / 3600)
  const m = Math.floor((totalSec % 3600) / 60)
  const s = totalSec % 60
  return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
}
//状态
function getMatchStatus(startStr, endStr) {
  if (!startStr || !endStr) return 'Error'
  const now = Date.now()
  const start = new Date(startStr).getTime()
  const end = new Date(endStr).getTime()
  if (now < start) return 'Waiting'
  if (now > end) return 'Ended'
  return 'Running'
}

const gameList=ref([])

const getGameInfo=async()=>{
    try{
        const res=await request.get('/getGameList')
        gameList.value=res.data
    }catch(err){
        console.log(err)
    }
}

const goP=(id)=>{
    try{
        router.push('/problem/'+id)
        localStorage.setItem("gameId",id)
    }catch(err){
        console.log(err)
    }
}
getGameInfo()
</script>

<template>
<div class="list-container">
<h2 style="text-align: center;margin-top: 50px;">Competitions List</h2>
<div class="list-cards">
<el-card style="border: none; min-width: 1000px; box-shadow: none; background-color: rgb(160, 180, 226);">
      <div class="card-inner">
        <span style="flex: 1;">Name</span>
        <span style="flex:1;text-align: center;">Date(duration)</span>
        <span style="flex: 1;text-align: right;white-space: nowrap;">status</span>
      </div>
    </el-card>
<el-card class="card" shadow="hover" v-for="(item) in gameList" :key="item.id" @click="goP(item.id)">
    <div class="card-inner">
        <span style="flex: 1;">{{ item.title }}</span>
        <span style="flex:1;text-align: center;">{{ item.startTime }}({{getDiffStr(item.startTime,item.endTime)}})</span>
        <span style="flex: 1;text-align: right;white-space: nowrap;">{{ getMatchStatus(item.startTime,item.endTime) }}</span>
    </div>
</el-card>
</div>
</div>
</template>

<style scoped>
.list-container{
    min-height: 80vh;
}
.list-cards{
    user-select: none;
    margin-top: 50px;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.card{
    align-items: center;
    min-width: 1000px;
    border-radius: 0%;
    border:none;
    background-color: rgb(293, 239, 252);
    transition: background-color 1s ease,transform 0.4s ease;
}
.card:hover{
    transform: scale(1.03);
    background-color: rgb(92, 241, 255);
}
.card-inner{
    display: flex;
    align-items: center;
    width:100%;
}
:deep(.el-card__body) {
  width: 100%;
  padding:16px;
  box-sizing: border-box !important;
}
</style>