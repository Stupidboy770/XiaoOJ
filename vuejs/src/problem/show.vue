<script setup>
import request from '@/utils/request';
import { ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
const route = useRoute()
const gameid = route.params.id
const js = ref({})
const pset = ref([])
const isStart=ref(false)

const getSingle = async () => {
    try {
        const res = await request.get('/getSingle',
            {
                params: {
                    id: gameid
                }
            }
        )
        js.value = res.data
    } catch (err) {
        console.log(err)
        return
    }
    isStart.value=true
    try {
        const res = await request.get(
            '/getProblemSet',
            {
                params: {
                    id: gameid
                }
            }
        )
        pset.value = res.data
        res.data.forEach(e => {
            localStorage.setItem(e.problemId, e.problemName)
        });
    } catch (err) {
        console.log(err)
    }
}

getSingle()
</script>

<template>
    <div v-if="!isStart">
        比赛还未开始        
    </div>
    <div class="game-container" v-else>

        <h1 style="text-align: center;">{{ js.title }}</h1>
        <el-card class="card-container" v-if="js.description != null">
            <p class="pd">比赛介绍</p>
            <div class="textdes">{{ js.description }}</div>
        </el-card>
        <el-card class="card-container" style="margin-top: 20px;">
            <p class="pd">题目集</p>
            <div class="problemSet">
                <h3 class="aproblem">题目名称</h3>
                <h3 class="aproblem">通过/提交</h3>
            </div>
            <div class="problemSet" v-for="(item, index) in pset" :key="index">
                <div style="display: flex; align-items: center; gap:8px;">
                    <span>{{ index + 1 }}.</span>
                    <router-link :to="'/problem/' + gameid + '/' + item.problemId" style="margin:0;">
                        <p style="margin: 0;">{{ item.problemName }}</p>
                    </router-link>
                </div>
                <p>{{ item.acceptSum }}/{{ item.submitSum }}</p>
            </div>
        </el-card>
    </div>
</template>

<style>
.game-container {
    min-height: 80vh;
    padding: 30px 250px;
}

.card-container {
    box-shadow: none;
    border: 0;
    border-radius: 0;
    padding: 0 50px;
}

.pd {
    border-bottom: 1px solid black;
    font-size: 22px;
}

.textdes {
    font-size: 19px;
    font-family: "SimSun";
}

.problemSet {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.aproblem {
    font-size: 15px;
}
</style>