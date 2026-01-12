import {createRouter, createWebHistory} from 'vue-router'
import AdminLayout from "@/views/layout/AdminLayout.vue";
import { ElMessage } from 'element-plus'

const router = createRouter({
    history: createWebHistory(),
    routes: getRoutes()
})

function getRoutes() {
    let defaultRoutes = [
        {
            path: '/admin',
            name: 'admin',
            component: AdminLayout,
            redirect: "/admin/home",
            meta: { requiresAuth: true, requiresAdmin: true },
            children: [{
                path: "home",
                name: "admin-home",
                component: () =>
                    import ('../views/admin/Home.vue'),
                meta: { requiresAuth: true, requiresAdmin: true }
            },
                {
                    path: 'editCurrentUser',
                    name: 'admin-editCurrentUser',
                    component: () =>
                        import ('../views/EditCurrentUser.vue'),
                    meta: { requiresAuth: true, requiresAdmin: true }
                },
                {
                    path: 'editPassword',
                    name: 'admin-editPassword',
                    component: () =>
                        import ('../views/EditPassword.vue'),
                    meta: { requiresAuth: true, requiresAdmin: true }
                },
                {
                    path: 'admin',
                    name: 'Admin',
                    component: () =>
                        import ('../views/admin/AdminManage.vue'),
                    meta: { requiresAuth: true, requiresAdmin: true }
                },
                {
                    path:'user',
                    name:'user',
                    component:() =>
                        import ('../views/admin/UserManage.vue'),
                    meta: { requiresAuth: true, requiresAdmin: true }
                },
                {
                    path: 'product',
                    name: 'AdminProduct',
                    component: () => import('@/views/admin/ProductManage.vue'),
                    meta: { title: '商品管理', requiresAuth: true, requiresAdmin: true }
                },
                {
                    path: 'order',
                    name: 'AdminOrder',
                    component: () => import('@/views/admin/OrderManage.vue'),
                    meta: { title: '订单管理', requiresAuth: true, requiresAdmin: true }
                },
            ]
        },
        {
            path: "/",
            name: "front-home",
            component: () =>
                import ('../views/front/Home.vue')
        },
        {   
            path: "/user/center", 
            name: "user-center", 
            component: () => 
                import ('../views/front/UserCenter.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/edit-profile",
            name: "edit-profile",
            component: () =>
                import ('../views/front/EditProfile.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/my-orders",
            name: "my-orders",
            component: () =>
                import ('../views/front/MyOrders.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/address-manage",
            name: "address-manage",
            component: () =>
                import ('../views/front/AddressManage.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/account-settings",
            name: "account-settings",
            component: () =>
                import ('../views/front/AccountSettings.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/product/:id",
            name: "product-detail",
            component: () =>
                import ('../views/front/ProductDetail.vue')
        },
        {
            path: "/cart",
            name: "shopping-cart",
            component: () =>
                import ('../views/front/Cart.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/checkout",
            name: "checkout",
            component: () =>
                import ('../views/front/Checkout.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: "/login",
            name: "login",
            component: () =>
                import ('../views/Login.vue')
        }, {
            path: "/register",
            name: "register",
            component: () =>
                import ('../views/Register.vue')
        }, {
            path: "/retrievePassword",
            name: "front-retrievePassword",
            component: () =>
                import ('../views/RetrievePassword.vue')
        },
        {
            path:'/404',
            name:'404',
            component: () => import ('../views/404.vue')
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/404'
        }
    ];
    
    console.log('getDynamicRoutes', defaultRoutes)
    return defaultRoutes;
}

// 路由守卫
router.beforeEach((to, from, next) => {
    // 获取token和用户信息
    const token = localStorage.getItem('token')
    const currentUserStr = localStorage.getItem('currentUser')
    const currentUser = currentUserStr ? JSON.parse(currentUserStr) : null
    
    // 白名单路由（不需要登录的页面）
    const whiteList = ['/login', '/register', '/retrievePassword', '/404', '/', '/product']
    
    // 检查是否在白名单中（包括动态路由）
    const isInWhiteList = whiteList.some(path => {
        if (path === '/product') {
            // 商品详情页是动态路由
            return to.path.startsWith('/product/')
        }
        return to.path === path
    })
    
    // 检查是否需要登录
    if (to.meta.requiresAuth) {
        if (!token || !currentUser) {
            // 未登录，跳转到登录页
            ElMessage.warning('请先登录')
            next({
                path: '/login',
                query: { redirect: to.fullPath } // 保存原目标路由，登录后跳回
            })
            return
        }
        
        // 检查是否需要管理员权限
        if (to.meta.requiresAdmin) {
            if (currentUser.type !== 'ADMIN') {
                // 非管理员访问管理员页面，跳转到首页
                ElMessage.error('您没有权限访问该页面')
                next('/')
                return
            }
        }
    }
    
    // 如果已登录，且访问登录页，根据用户类型跳转
    if (to.path === '/login' && token && currentUser) {
        if (currentUser.type === 'ADMIN') {
            next('/admin')
        } else {
            next('/')
        }
        return
    }
    
    // 普通用户访问管理员页面，跳转到首页
    if (to.path.startsWith('/admin') && currentUser && currentUser.type !== 'ADMIN') {
        ElMessage.error('您没有权限访问该页面')
        next('/')
        return
    }
    
    next()
});
export default router