import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs'
import { createWebHistory, createRouter } from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'Login',
        component: () => import('../login/Login.vue')
    },
    {
        path: '/list',
        name: 'list',
        component: () => import('../competition/list.vue')
    },
    {
        path: '/status',
        name: 'status',
        component: () => import('../status/userstatus.vue')
    },
    {
        path: '/rank/:id',
        name: 'rank',
        component: () => import('../rank/rank.vue')
    },
    {
        path: '/mg',
        name: 'Management',
        component: () => import('../admin/management.vue')
    },
    {
        path: '/problem/:id/:io',
        name: 'problemid',
        component: () => import('../problem/showproblem.vue')
    },
    {
        path: '/problem/:id',
        name: 'problem',
        component: () => import('../problem/show.vue')
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    try {
        const token = localStorage.getItem('token')
        const userId = localStorage.getItem('userId')
        if (to.path !== '/' && !token && !userId) {
            next({ name: 'Login' })
        } else {
            next()
        }
    } catch (err) {
        console.error('localStorage异常', err)
        next({ name: 'Login' })
    }
})

export default router
