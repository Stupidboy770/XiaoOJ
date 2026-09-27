import axios from "axios";
import router from '@/utils/router'

const baseURL = '/api'

const request = axios.create({
    baseURL: baseURL,
    timeout: 5000
})

const adminRequest = axios.create({
    baseURL: baseURL + '/admin',
    timeout: 5000
})

const copyInterceptors = (source, target) => {
    target.interceptors.request.handlers = [...source.interceptors.request.handlers]
    target.interceptors.response.handlers = [...source.interceptors.response.handlers]
}
copyInterceptors(request, adminRequest)

request.interceptors.request.use(
    (config) => {
        const noTokenUrl = ['/Login']
        if (!noTokenUrl.includes(config.url)) {
            const token = localStorage.getItem('token')
            if (token) {
                config.headers.token = token
                config.headers.userId = localStorage.getItem('userId')
            }
        }
        return config
    }, (err) => {
        return Promise.reject(err)
    }
)

request.interceptors.response.use(
    (response) => {
        return response
    }, (err) => {
        if (err.response) {
            const status = err.response.status
            if (status === 401) {
                localStorage.removeItem('token')
                localStorage.removeItem('token')
                localStorage.removeItem('isA')
                localStorage.removeItem('gameId')
                localStorage.removeItem('userId')
                localStorage.removeItem('isA')
                router.push('/')
            }
        }
        return Promise.reject(err)
    }
)

copyInterceptors(request, adminRequest)

export { request, adminRequest }

export default request