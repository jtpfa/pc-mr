<template>
    <main class="container my-5">
        <h1>Bestellungen</h1>

        <account-orders />
    </main>
</template>

<script>
/**
 * @component OrdersPage
 * @desc Customer orders page
 * @author Jonas Pfannkuche
 */

import AccountOrders from '~/components/shop/account/orders'

export default {
    components: { AccountOrders },
    middleware: ['auth'],
    head() {
        return {
            title: `${this.$route.name.replace(/^\w/, c => c.toUpperCase())} – PC Masterrace`,
        }
    },
}
</script>

<style scoped></style>
