<script setup>
import { adminRequest } from '@/utils/request';
import router from '@/utils/router';
import { ElMessage } from 'element-plus';
import { getColumnById } from 'element-plus/es/components/table/src/util.mjs';
import { ref } from 'vue';
const swi = ref(false)
const allProblem = ref([])
const activeProblem = ref('')
const updateProblemData = ref({})
/**


 */
const getList = async () => {
    try {
        const resp = await adminRequest.get('/getAllProblem')
        allProblem.value = resp.data
    } catch (err) {
        console.log(err)
        if (err.response?.status === 401) router.push('/')
    }
}

const handleUpload = async (opt) => {
    const file = opt.file
    if (!file) return
    const formData = new FormData()
    formData.append('files', file)
    formData.append('problemId', updateProblemData.value.problemId)
    try {
        await adminRequest.post('/postExample', formData)
        ElMessage.success('success')
    } catch (err) {
        console.error('上传失败', err)
        opt.onError(err)
    }
}

const updateAProblem = async () => {
    try {
        const res = await adminRequest.post('/updateProblem', updateProblemData.value)
    } catch (err) { console.log(err) }
    getList()
    activeProblem.value = ''
}

const deleteAProblem = async () => {
    try {
        const res = await adminRequest.delete('/deleteProblem', {
            params: {
                "id": updateProblemData.value.problemId
            }
        })
        ElMessage.success('i will come back')
    } catch (err) { console.log(err) }
    getList()
    activeProblem.value = ''
}

//添加题目
const addProblemData = ref({})
const addAProblem = async () => {
    try {
        const res = await adminRequest.post('/addProblem', addProblemData.value)
    } catch (err) { console.log(err) }
    getList()
    activeProblem.value = ''
    // clr()
}

const cyInf = (item) => updateProblemData.value = item
const clr = () => updateProblemData.value = {}
getList()
/**
 * 
 * 
 * 
 */

const dialog = ref(false)
const allCompetition = ref([])

const oneSet = ref([])
const getCompetitionList = async () => {
    try {
        const res = await adminRequest.get('/getAllCompetition')
        allCompetition.value = res.data
        console.log(res.data)
    } catch (err) {
        console.log(err)
    }
}

const getACompetition = async (item) => {
    chosenOne.value = item
    chosenOne.value.startTime = new Date(item.startTime)
    chosenOne.value.endTime = new Date(item.endTime)
    dialog.value = true
    getGameProblem(item.id)
}

const getGameProblem = async (competitionId) => {
    try {
        const res = await adminRequest.get('/getGameProblem',
            {
                params: {
                    "competitionId": competitionId
                }
            }
        )
        oneSet.value = res.data
    } catch (err) { console.log(err) }
}

const chosenOne = ref({})
const updateGame = async () => {
    try {
        const res = await adminRequest.post('/changeCompetition', {
            "id": chosenOne.value.id,
            "title": chosenOne.value.title,
            "startTime": chosenOne.value.startTime,
            "endTime": chosenOne.value.endTime,
            "description": chosenOne.value.description
        })
        ElMessage.success('succeed!')
    } catch (err) {
        console.log(err)
        ElMessage.error('update failed')
    }
    getCompetitionList()
    dialog.value = false
}

const deGame = async () => {
    try {
        const res = await adminRequest.delete('/deleteCompetition', {
            params: {
                "competitionId": chosenOne.value.id
            }
        })
        ElMessage.success('未出现异常')
    } catch (err) {
        console.log(err)
        ElMessage.error('deGame failed')
    }
    getCompetitionList()
    dialog.value = false
}

const deValue = ref('')
const deAProblemInSet = async () => {
    try {
        const res = await adminRequest.delete('/deleteToProblem',
            {
                params: {
                    "competitionId": chosenOne.value.id,
                    "problemId": deValue.value
                }
            }
        )
        ElMessage.success('未出现异常')
    } catch (err) { console.log(err) }
    getGameProblem(chosenOne.value.id)
    deValue.value = ''
}

const addValue = ref('')
const addAProblemInSet = async () => {
    try {
        const res = await adminRequest.post('/addToProblem', null,
            {
                params: {
                    "competitionId": chosenOne.value.id,
                    "problemId": addValue.value
                }
            }
        )
        ElMessage.success('未出现异常')
    } catch (err) {
        console.log(err)
        ElMessage.error('重复或出现其他错误')
    }
    getGameProblem(chosenOne.value.id)
    addValue.value = ''
}

const dialog2 = ref(false)
const newCompetition = ref({})
const addCompetition = async () => {
    try {
        const res = await adminRequest.post('/addCompetition', newCompetition.value)
        ElMessage.success('未出现异常')
        dialog2.value = false
    } catch (err) {
        console.log(err)
        ElMessage.error('出现其他错误')
    }
    dialog2.value = false
    getCompetitionList()
}

getCompetitionList()
</script>
<!--
problem
展示所有问题-添加问题-删除问题-更新测试数据
competition
展示所有比赛-添加比赛-删除比赛-选取问题
-->
<template>
    <div class="manage-container">
        <div style="display: flex; align-items: center; justify-content: center;"><el-switch v-model=swi size="large"
                active-text="Competition" inactive-text="Problem" /></div>
        <div class="problem-page" v-if="!swi">
            <el-collapse v-model="activeProblem" accordion>
                <el-collapse-item title="添加题目" name="-1">
                    <el-form>
                        <el-form-item label="题目名称">
                            <el-input v-model="addProblemData.problemName" />
                        </el-form-item>
                        <el-form-item label="时间限制(ns)">
                            <el-input v-model="addProblemData.timeLimit" />
                        </el-form-item>
                        <el-form-item label="空间限制(byte)">
                            <el-input v-model="addProblemData.memoryLimit" />
                        </el-form-item>
                        <el-form-item label="题目描述.md">
                            <el-input type="textarea" v-model="addProblemData.problemDescription" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输入描述.md">
                            <el-input type="textarea" v-model="addProblemData.problemInputDescription" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输出描述.md">
                            <el-input type="textarea" v-model="addProblemData.problemOutputDescription" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输入样例">
                            <el-input type="textarea" v-model="addProblemData.problemInput" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输出样例">
                            <el-input type="textarea" v-model="addProblemData.problemOutput" :rows="7" />
                        </el-form-item>
                        <el-form-item label="添加题目(创建完题目才能加测试数据)">
                            <el-button type="primary" @click="addAProblem">添加!</el-button>
                        </el-form-item>
                    </el-form>
                </el-collapse-item>
                <el-collapse-item v-for="item in allProblem" :key="item.problemId"
                    :title="item.problemId + '.' + item.problemName" :name="item.problemId" @click="cyInf(item)">
                    <el-form>
                        <el-form-item label="题目名称">
                            <el-input v-model="updateProblemData.problemName" />
                        </el-form-item>
                        <el-form-item label="时间限制(ns)">
                            <el-input v-model="updateProblemData.timeLimit" />
                        </el-form-item>
                        <el-form-item label="空间限制(byte)">
                            <el-input v-model="updateProblemData.memoryLimit" />
                        </el-form-item>
                        <el-form-item label="题目描述.md">
                            <el-input type="textarea" v-model="updateProblemData.problemDescription" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输入描述.md" :rows="7">
                            <el-input type="textarea" v-model="updateProblemData.problemInputDescription" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输出描述.md">
                            <el-input type="textarea" v-model="updateProblemData.problemOutputDescription" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输入样例">
                            <el-input type="textarea" v-model="updateProblemData.problemInput" :rows="7" />
                        </el-form-item>
                        <el-form-item label="题目输出样例">
                            <el-input type="textarea" v-model="updateProblemData.problemOutput" :rows="7" />
                        </el-form-item>
                        <el-form-item label="更新题目">
                            <el-button type="primary" @click="updateAProblem">更新数据</el-button>
                        </el-form-item>
                        <el-form-item label="上传测试用例(.zip)_(要求.in,.out一一对应上传后会覆盖原来的数据)">
                            <el-upload action="" :limit="1" :http-request="handleUpload" accept=".zip">
                                <el-button type="primary">上传!</el-button>
                            </el-upload>
                        </el-form-item>
                        <el-form-item label="删除题目">
                            <el-button type="danger" @click="deleteAProblem">删除!</el-button>
                        </el-form-item>
                    </el-form>
                </el-collapse-item>
            </el-collapse>
        </div>
        <div class="competition-page" v-if="swi">

            <el-card>
                <p style="margin: 50px 0; text-align: center; border:1px dotted black;" @click="dialog2 = true">添加比赛</p>
                <p v-for="item in allCompetition" style="margin: 50px 0; text-align: center; border:1px dotted black;"
                    :key="item.id" @click="getACompetition(item)">{{ item.title }}</p>
            </el-card>

            <el-dialog v-model="dialog2">
                <el-form>
                    <el-form-item label="比赛标题">
                        <el-input v-model="newCompetition.title" />
                    </el-form-item>
                    <el-form-item label="比赛描述">
                        <el-input v-model="newCompetition.description" type="textarea" :rows="7" />
                    </el-form-item>
                    <el-form-item label="开始时间">
                        <el-date-picker v-model="newCompetition.startTime" type="datetime"
                            value-format="YYYY-MM-DDTHH:mm:ss" placeholder="Select date and time" />
                    </el-form-item>
                    <el-form-item label="结束时间">
                        <el-date-picker v-model="newCompetition.endTime" type="datetime"
                            value-format="YYYY-MM-DDTHH:mm:ss" placeholder="Select date and time" />
                    </el-form-item>
                    <el-button type="primary" @click="addCompetition">新建比赛!</el-button>
                </el-form>
            </el-dialog>

            <el-dialog v-model="dialog">
                <el-form>
                    <el-form-item label="比赛标题">
                        <el-input v-model="chosenOne.title" />
                    </el-form-item>
                    <el-form-item label="比赛描述">
                        <el-input v-model="chosenOne.description" type="textarea" :rows="7" />
                    </el-form-item>
                    <el-form-item label="提交更改">
                        <el-button type="primary" @click="updateGame">提交!</el-button>
                        <el-button type="danger" @click="deGame">删除该比赛</el-button>
                    </el-form-item>
                    <p style="border-bottom: 1px solid black;"></p>
                    <el-form-item>
                        <el-table :data="oneSet" style="width: 100%">
                            <el-table-column label="题目集" prop="problemName" />
                        </el-table>
                    </el-form-item>
                    <el-form-item label="删除一个题目">
                        <el-select v-model="deValue" placeholder="Select" style="width: 240px">
                            <el-option v-for="item in oneSet" :key="item.problemId" :label="item.problemName"
                                :value="item.problemId" />
                        </el-select>
                        <el-button type="danger" @click="deAProblemInSet">删除</el-button>
                    </el-form-item>
                    <el-form-item label="添加一个题目">
                        <el-select v-model="addValue" placeholder="Select" style="width: 240px">
                            <el-option v-for="item in allProblem" :key="item.problemId" :label="item.problemName"
                                :value="item.problemId" />
                        </el-select>
                        <el-button type="primary" @click="addAProblemInSet">添加</el-button>
                    </el-form-item>
                </el-form>
            </el-dialog>
        </div>
    </div>
</template>
<style scoped>
.manage-container {
    padding: 0 200px;
}
</style>
