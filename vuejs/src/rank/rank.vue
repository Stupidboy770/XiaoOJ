<script setup>
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { ref } from 'vue';

const alldata = ref([])
const pset = ref([])
const beginTime = ref('')

const getList = async (startIndexe = 0, endIndexe = 3) => {
    try {
        const res = await request.get(
            '/getRank',
            {
                params: {
                    competitionId: localStorage.getItem('gameId'),
                    //左闭右开
                    startIndex: startIndexe,
                    endIndex: endIndexe
                }
            }
        )
        const res2 = await request.get(
            '/getProblemSet',
            {
                params: {
                    id: localStorage.getItem('gameId')
                }
            }
        )
        const res3 = await request.get(
            '/getSingle',
            {
                params: {
                    id: localStorage.getItem('gameId')
                }
            }
        )
        beginTime.value = res3.data.startTime
        pset.value = res2.data
        alldata.value = res.data
        if (alldata.value.at(-1) != null) {
            alldata.value.unshift(alldata.value.at(-1))
        }
        alldata.value.pop()
        for (const single of alldata.value) {
            const sumTime = []
            let sumMM = 0
            for (const problem of single.rankProblems) {
                if (problem.ac) {
                    sumTime.push(problem.acTime)
                    sumMM += problem.beforeAcSubmit * 20
                }
            }
            single.sumTime = addHhMmSs(calcTimeDiff(beginTime.value, sumTime), sumMM);
        }
    } catch (err) {
        ElMessage.info('比赛还未开始')
        console.log(err)
    }
}

const calcTimeDiff = (beginTime, acTimeArr) => {
    const baseTs = new Date(beginTime).getTime()
    let totalSec = 0
    for (const acTime of acTimeArr) {
        const currTs = new Date(acTime).getTime()
        const diffMs = currTs - baseTs
        const diffSec = diffMs > 0 ? Math.floor(diffMs / 1000) : 0
        totalSec += diffSec
    }
    const hours = String(Math.floor(totalSec / 3600)).padStart(2, '0')
    const minutes = String(Math.floor((totalSec % 3600) / 60)).padStart(2, '0')
    const seconds = String(totalSec % 60).padStart(2, '0')
    return `${hours}:${minutes}:${seconds}`
}

const addHhMmSs = (timeStr, addMin) => {
    const [h, m, s] = timeStr.split(':').map(Number)
    let total = h * 3600 + m * 60 + s + addMin * 60
    if (total < 0) total = 0
    const hours = String(Math.floor(total / 3600)).padStart(2, '0')
    const minutes = String(Math.floor((total % 3600) / 60)).padStart(2, '0')
    const seconds = String(total % 60).padStart(2, '0')
    return `${hours}:${minutes}:${seconds}`
}

const cellStyleFn = ({ row, columnIndex }) => {
    if (columnIndex > 3) {
        const problemIdx = columnIndex - 4
        if (row.rankProblems?.[problemIdx]?.first) {
            return { backgroundColor: 'rgb(193, 197, 195)' }
        }
        if (row.rankProblems?.[problemIdx]?.ac) {
            return { backgroundColor: '#d4edda' }
        }
        if (row.rankProblems?.[problemIdx]?.beforeAcSubmit > 0) {
            return { backgroundColor: 'rgb(224, 124, 124)' }
        }
    }
    return {}
}

const pageTotal = ref(3)
const currentPage = ref(1)

const handlePageChange = (newPage) => {
    currentPage.value = newPage
    const startIndex = (newPage - 1) * 50
    let endIndex = startIndex + 50
    if (endIndex > pageTotal.value) endIndex = pageTotal.value
    getList(startIndex, endIndex)
}

const getAllpeople = async () => {
    try {
        const res = await request.get('/getUser')
        pageTotal.value = res.data
        getList(0, Math.max(1, Math.min(pageTotal.value, 50)))
    } catch (err) {
        console.log(err)
    }
}
getAllpeople()
</script>

<template>
    <div class="rank-container">
        <div class="inf">
            <el-table :data="alldata" width="100%" :cell-style="cellStyleFn" border>
                <el-table-column label="Rank" width="70">
                    <template #default="{ row, $index }">
                        {{ row.rank }}
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="Name" width="200" />
                <el-table-column prop="acSum" label="Passed" />
                <el-table-column prop="sumTime" label="Penalty" width="100" />
                <el-table-column v-for="(col, index) in pset" :key="index">
                    <template #header>
                        <div style="text-align: center; font-size: large;">
                            {{ index + 1001 }}
                        </div>
                        <div style="text-align: center; font-size: small;">
                            {{ col.acceptSum }}/{{ col.submitSum }}
                        </div>
                    </template>
                    <template #default="scope">
                        <div>
                            <div style="text-align: center; font-size: larger;">
                                <span v-if="scope.row.rankProblems[index].ac">+</span>
                                <span
                                    v-if="!scope.row.rankProblems[index].ac && scope.row.rankProblems[index].beforeAcSubmit > 0">-</span>
                                <span v-if="scope.row.rankProblems[index].beforeAcSubmit > 0">{{
                                    scope.row.rankProblems[index].beforeAcSubmit }}</span>
                            </div>
                            <div v-if="scope.row.rankProblems?.[index]?.ac"
                                style="text-align: center; font-size: smaller;">
                                {{ calcTimeDiff(beginTime, [scope.row.rankProblems[index].acTime]) }}
                            </div>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
    <div style="display: flex; justify-content: center; margin: 20px 0;">
        <el-pagination layout="prev, pager, next" :total="pageTotal" @current-change="handlePageChange"
            :page-size="50" />
    </div>
</template>

<style scoped>
.rank-container {
    display: flex;
    align-items: center;
    justify-content: center;
}

:deep(.inf .el-table__header th) {
    background-color: rgb(151, 234, 213);
}

:deep(.el-table .el-table__cell) {
    height: 80px;
}
</style>