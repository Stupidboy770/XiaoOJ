<script setup>
import request from '@/utils/request';
import router from '@/utils/router';
import { dayjs, ElMessage, ElMessageBox } from 'element-plus';
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import katex from 'katex'
import 'katex/dist/katex.min.css'

function renderMdWithKatex(rawMd) {
  if (!rawMd) return ''
  let str = rawMd

  str = str.replace(/\$\$([\s\S]*?)\$\$/g, (match, expr) => {
    try {
      return katex.renderToString(expr.trim(), { displayMode: true })
    } catch (e) {
      return match
    }
  })

  str = str.replace(/\$([^\$]+?)\$/g, (match, expr) => {
    try {
      return katex.renderToString(expr.trim(), { displayMode: false })
    } catch (e) {
      return match
    }
  })

  const html = marked.parse(str)
  return DOMPurify.sanitize(html)
}

const route = useRoute()
const id = route.params.id;
const idd = route.params.io;
const Aproblem = ref({})
const MB = (mb) => {
    return Math.round(mb / 1024 / 1024)
}
const NS = (ns) => {
    const sec = ns / 1000000000
    return Number(sec.toFixed(2))
}
const getProblem = async () => {
    try{
        const vaildation= await request.get('/getSingle',
            {
                params: {
                    id: id
                }
            }
        )
    }catch(err){
        ElMessage.error('unknown')
        console.log(err)
        return;
    }
    try {
        const res = await request.get('/getAProblem'
            , {
                params: {
                    userId: localStorage.getItem('userId'),
                    problemId: idd
                }
            }
        )
        Aproblem.value = res.data
    } catch (err) {
        console.log(err)
    }
}
getProblem()

const problemDescHtml = computed(() => renderMdWithKatex(Aproblem.value?.problemDescription ?? ''))
const inputDescHtml = computed(() => renderMdWithKatex(Aproblem.value?.problemInputDescription ?? ''))
const outputDescHtml = computed(() => renderMdWithKatex(Aproblem.value?.problemOutputDescription ?? ''))
const inputSampleHtml = computed(() => renderMdWithKatex(Aproblem.value?.problemInput ?? ''))
const outputSampleHtml = computed(() => renderMdWithKatex(Aproblem.value?.problemOutput ?? ''))

const dialog = ref(false)
const codeText = ref('')
const chosenL = ref('')
const options = [
    {
        chosenL: 'CPP',
        label: 'C++',
    },
    {
        chosenL: 'JAVA',
        label: 'Java(Main.java)',
    },
    {
        chosenL: 'PYTHON',
        label: 'Python',
    }
]
const openPrompt = () => {
    codeText.value = ''
    dialog.value = true
}
const handleConfirm = async () => {
    if (chosenL.value === '') {
        ElMessage.error("请选择一门编程语言")
    } else {
        dialog.value = false
        codeText.value = codeText.value.replaceAll('\r', '')
        try {
            ElMessage.info('可在Status中查看判题状态')
            const res = await request.post('/gojudge', {
                userId: localStorage.getItem('userId'),
                problemId: idd,
                competitionId: id,
                context: codeText.value,
                languageRun: chosenL.value,
                submittedTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
            })
            if (res.data.problemStatus === 'Accepted') ElMessage.success('Accepted!')
            else ElMessage.error(res.data.problemStatus + '!')
        } catch (err) {
            ElMessage.error('提交失败')
            console.log(err)
        }
    }
}
</script>
<template>
    <div class="show-container">
        <h2 style="text-align: center;">{{ Aproblem.problemName }}</h2>
        <p style="text-align: center;white-space:pre;">MemoryLimit:{{ MB(Aproblem.memoryLimit) }}MB
            &emsp;&emsp;&emsp;TimeLimit:{{ NS(Aproblem.timeLimit) }}s</p>
        <el-card class="card-container">
            <p class="pd">问题描述</p>
            <div v-html="problemDescHtml"></div>
            <p class="pd">输入描述</p>
            <div v-html="inputDescHtml"></div>
            <p class="pd">输出描述</p>
            <div v-html="outputDescHtml"></div>
            <p class="pd">输入样例</p>
            <div v-html="inputSampleHtml"></div>
            <p class="pd">输出样例</p>
            <div v-html="outputSampleHtml"></div>
            <h2 style="text-align: end; color: #409EFF; user-select: none;" @click="openPrompt">Submit!</h2>
        </el-card>
        <el-dialog v-model="dialog" :title="Aproblem.problemName" width="800px">
            <div class="limit-info">
                MemoryLimit:{{ MB(Aproblem.memoryLimit) }}MB&emsp;&emsp;
                TimeLimit:{{ NS(Aproblem.timeLimit) }}s
                <el-select v-model="chosenL" placeholder="Select" style="width: 240px;margin-left: 100px;">
                    <el-option v-for="item in options" :key="item.chosenL" :label="item.label" :value="item.chosenL" />
                </el-select>
            </div>
            <el-input v-model="codeText" type="textarea" :rows="15" placeholder="your code..."
                style="margin-top:12px;font-family:Consolas,monospace;font-size:14px" spellcheck="false" />
            <template #footer>
                <el-button @click="dialog = false">cancel</el-button>
                <el-button type="primary" @click="handleConfirm">confirm</el-button>
            </template>
        </el-dialog>
    </div>
</template>
<style scoped>
.show-container {
    background-color: rgb(244, 251, 251);
    padding: 30px 250px;
    padding-bottom: 50px;
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
.limit-info {
    font-size: 14px;
    color: #606266;
    margin-bottom: 10px;
}
</style>
