import axios from "axios";
import router from "../router";
import {ElMessage} from "element-plus";

// 设置 Axios 的默认基础 URL
axios.defaults.baseURL = import.meta.env.VITE_APP_API_URL;

// 创建 Axios 实例
const http = axios.create({
    timeout: 5000, headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
http.interceptors.request.use(config => {
    if (localStorage.getItem("token")) {
        config.headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    return config;
})
;

// 响应拦截器
http.interceptors.response.use(response => {
    const responseData = response.data;
    // 如果是200，返回数据，否则视为错误
    if (responseData.code === 200) {
        return responseData;
    } else {
        // 非200状态码，视为错误
        const errorMsg = responseData.msg || "请求失败";
        ElMessage({message: errorMsg, type: "error"});
        return Promise.reject(new Error(errorMsg));
    }
}, error => {
    const status = error.response?.status;
    const errorData = error.response?.data;
    let errorMsg = "未知错误";
    
    switch (status) {
        case 401:
            localStorage.removeItem("token");
            errorMsg = "请先登录";
            ElMessage({message: errorMsg, type: "error"});
            router.push("/login");
            break;
        case 409:
            errorMsg = errorData.data || errorData.msg || "请求冲突";
            ElMessage({message: errorMsg, type: "error"});
            break;
        case 404:
            errorMsg = "接口未找到";
            ElMessage({message: errorMsg, type: "error"});
            break;
        case 500:
            errorMsg = "服务异常";
            ElMessage({message: errorMsg, type: "error"});
            break;
        default:
            errorMsg = (errorData && errorData.msg) || error.message || "请求失败";
            ElMessage({message: errorMsg, type: "error"});
    }
    return Promise.reject(new Error(errorMsg));
});
// 打印环境变量
console.log("环境:", import.meta.env.NODE_ENV);
console.log("服务器:", import.meta.env.VUE_APP_SERVER);
console.log("所有环境变量:", import.meta.env);

export const  request=http;
export default http;

