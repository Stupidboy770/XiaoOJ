<script setup>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'


const tableData = ref([])

const formatMemory = ({ memory }) => {
    if (!memory) return '-'
    return (memory / 1024 / 1024).toFixed(2)
}


const formatRunTime = ({ runTime }) => {
    if (!runTime) return '-'
    return (runTime / 1000000).toFixed(2)
}


const getList = async (offsetr, limitr) => {
    try {
        const res = await request.get('/getUserStatus',
            {
                params: {
                    competitionId: localStorage.getItem('gameId'),
                    problemId: problemId.value,
                    selectUserId: selectUserId.value,
                    language: language.value,
                    offset: offsetr,
                    limit: limitr
                }
            }
        )
        res.data.forEach(e => {
            e.problemId = localStorage.getItem(e.problemId)
        });
        pageTotal.value=Number(res.headers['count'])
        tableData.value = res.data
    } catch (err) {
        console.log(err)
    }
}

const transmit = (code) => {
    codeText.value = code
    dialog.value = true
}

const handlePageChange = (newPage) => {
    currentPage.value = newPage
    const offset = (newPage - 1) * 20
    getList(offset, 20)
}

const queryStatus=async()=>{
    currentPage.value=1
    getList(0,20)
}

const clr=()=>{
    selectUserId.value=null
    problemId.value=null
    language.value=null
}

const currentPage = ref(1)
const dialog = ref(false)
const codeText = ref('')
const pageTotal = ref(0)

//表单数据
const selectUserId = ref(null)
const problemId = ref(null)
const language = ref(null)

const langArr = [{ value: 'java' }, { value: 'cpp' }, { value: 'python' }]

const problemSet=ref([])

const getPSet=async()=>{
    try{
        const res = await request.get(
            '/getProblemSet',
            {
                params: {
                    id: localStorage.getItem('gameId')
                }
            }
        )
        problemSet.value=res.data
    }catch(err){
        ElMessage.info("比赛还未开始")
        console.log(err)
    }
}

onMounted(() => {
    getPSet()
    getList(0, 20)
})

</script>


<template>
    <div class="selectC">
        <div class="sel-inner">
            <span>用户ID</span>
            <el-input v-model="selectUserId" style=" width: 240px" />
        </div>
        <div class="sel-inner">
            <span>题目</span>
            <el-select v-model="problemId" style=" width: 240px" placeholder="">
                <el-option v-for="item in problemSet" :key="item.problemId" :label="item.problemName" :value="item.problemId"/>
            </el-select>
        </div>
        <div class="sel-inner">
            <span>语言</span>
            <el-select v-model="language" style=" width: 240px" placeholder="">
                <el-option v-for="item in langArr" :key="item.value" :label="item.value" :value="item.value" />
            </el-select>
        </div>
        <el-button class="sel-inner" type="primary" @click="queryStatus">
            查询
        </el-button>
        <el-button class="sel-inner" type="info" @click="clr">
            清空
        </el-button>
    </div>
    <div class="table-wrap">
        <el-table :data="tableData" border stripe style="width:100%" class="big-font-table">
            <el-table-column prop="id" label="提交ID" width="80" />
            <el-table-column prop="userId" label="用户ID" />
            <el-table-column prop="problemId" label="题目" />
            <el-table-column prop="submittedTime" label="提交时间" />
            <el-table-column label="判题结果">
                <template #default="{ row }">
                    <span class="default-color" :class="{
                        accolor: row.result === 'Accepted',
                        cecolor: row.result === 'Compile error'
                    }">{{ row.result }}</span>
                </template>
            </el-table-column>
            <el-table-column label="内存(MB)" :formatter="formatMemory" />
            <el-table-column label="耗时(ms)" :formatter="formatRunTime" />
            <el-table-column label="语言">
                <template #default="{ row }">
                    <span style="color: blue;text-decoration: underline;user-select: none;" @click="transmit(row.code)">
                        {{ row.language }}
                    </span>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog v-model="dialog" width="800px">
            <el-input v-model="codeText" type="textarea" :rows="15"
                style="margin-top:12px;font-family:Consolas,monospace;font-size:14px" spellcheck="false" />
            <template #footer>
                <el-button @click="dialog = false">cancel</el-button>
            </template>
        </el-dialog>
        <div style="display: flex; justify-content: center; margin: 20px 0;">
            <el-pagination layout="prev, pager, next" :total="pageTotal" @current-change="handlePageChange"
                :page-size="20" />
        </div>
    </div>
</template>



<style scoped>
.selectC {
    margin: 20px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 24px;
}

.sel-inner {
    display: flex;
    align-items: center;
    gap: 8px;
}

.table-wrap {
    padding: 0px 100px;
    min-height: 83vh;
}

.default-color {
    color: red;
}

.accolor {
    color: rgb(11, 239, 11);
}

.cecolor {
    color: rgb(167, 167, 92);
}

:deep(.big-font-table .el-table__cell) {
    font-size: 16px;
}

:deep(.big-font-table .el-table__header th) {
    font-size: 16px;
}
</style>
