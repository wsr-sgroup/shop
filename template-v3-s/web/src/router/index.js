import {createRouter, createWebHistory} from 'vue-router'
import AdminLayout from "@/views/layout/AdminLayout.vue";

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
            children: [{
                path: "home",
                name: "admin-home",
                component: () =>
                    import ('../views/admin/Home.vue')
            },
                {
                    path: 'editCurrentUser',
                    name: 'admin-editCurrentUser',
                    component: () =>
                        import ('../views/EditCurrentUser.vue')
                },
                {
                    path: 'editPassword',
                    name: 'admin-editPassword',
                    component: () =>
                        import ('../views/EditPassword.vue')
                },
                {
                    path: 'admin',
                    name: 'Admin',
                    component: () =>
                        import ('../views/admin/AdminManage.vue')
                },
                {
                    path:'user',
                    name:'user',
                    component:() =>
                        import ('../views/admin/UserManage.vue')
                },
                {
                    path: '/admin/product',
                    name: 'AdminProduct',
                    component: () => import('@/views/admin/ProductManage.vue'),
                    meta: { title: '商品管理' }
                }
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
                import ('../views/front/UserCenter.vue') 
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
                import ('../views/front/Cart.vue')
        },
        {
            path: "/checkout",
            name: "checkout",
            component: () =>
                import ('../views/front/Checkout.vue')
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

router.beforeEach((to, from, next) => {
    next();
});
export default router