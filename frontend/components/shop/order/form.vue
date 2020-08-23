<template>
    <b-form ref="form" novalidate @submit.prevent.stop="onSubmit">
        <order-headline />

        <div class="row mt-5">
            <div class="col-md-7 mb-0">
                <order-step1 v-if="step === 1" />

                <order-step2 v-else-if="step === 2" />

                <order-step3 v-else-if="step === 3" />

                <order-invalid-state v-else-if="step < 1 || step > 4" />
            </div>
            <div v-if="step >= 1 && step <= 3" class="col-md-5">
                <cart-summary class="mb-5 mb-md-0" :error="error">
                    <b-button
                        v-if="step === 3"
                        class="d-flex justify-content-center w-100 align-items-center my-3"
                        :disabled="loading"
                        size="lg"
                        type="submit"
                        variant="primary"
                    >
                        <b-spinner v-if="loading" small></b-spinner>
                        Jetzt kaufen
                    </b-button>
                </cart-summary>
            </div>
        </div>

        <order-pagination v-if="step >= 1 && step <= 3" :loading="loading" @back="stepBack" />
    </b-form>
</template>

<script>
import CartSummary from '~/components/shop/cart/summary'
import OrderHeadline from '~/components/shop/order/headline'
import OrderPagination from '~/components/shop/order/pagination'
import OrderInvalidState from '~/components/shop/order/steps/invalidState'
import OrderStep1 from '~/components/shop/order/steps/step1/step1'
import OrderStep2 from '~/components/shop/order/steps/step2/step2'
import OrderStep3 from '~/components/shop/order/steps/step3/step3'

export default {
    name: 'OrderForm',
    components: { OrderStep3, OrderStep2, OrderStep1, OrderInvalidState, OrderPagination, OrderHeadline, CartSummary },
    data() {
        return {
            loading: false,
            error: '',
        }
    },
    computed: {
        cart() {
            return this.$store.state.shoppingcart.cart
        },
        order() {
            return this.$store.state.order
        },
        step: {
            get() {
                return this.$store.state.order.step
            },
            set(step) {
                this.$store.commit('order/updateOrderInformation', { key: 'step', data: step })
            },
        },
    },
    methods: {
        stepBack() {
            this.$refs.form.classList.remove('was-validated')
            this.step -= 1
            this.$router.push({ path: '/bestellung', query: { step: this.step } })
            window.scrollTo(0, 0)
        },
        async onSubmit(event) {
            this.$refs.form.classList.remove('was-validated')
            this.loading = true
            this.error = ''
            if (!this.$refs.form.checkValidity()) {
                this.$refs.form.classList.add('was-validated')
                event.preventDefault()
                event.stopPropagation()

                this.loading = false

                return
            }
            // do not show validation state if everything is fine
            // because the next step would be validated as well

            if (this.step === 3) {
                await this.submitOrder()

                if (this.error.length > 0) {
                    this.loading = false
                    return
                }

                this.step += 1
                this.$router.push('/bestell-bestaetigung')
            } else {
                this.step += 1
                this.$router.push({ path: '/bestellung', query: { step: this.step } })
                window.scrollTo(0, 0)
            }

            this.loading = false
        },
        async submitOrder() {
            try {
                const shippingAddress = this.getAddress('shipping')
                const invoiceAddress = this.order.differentInvoiceAddress ? this.getAddress('invoice') : shippingAddress
                const orderItems = this.cart.map(item => {
                    return { articleId: item.id, quantity: item.quantity }
                })
                const paymentMethod = this.order.paymentMethod.id
                const shippingMethod = this.order.shippingMethod.id

                await this.$api.placeOrder(
                    { orderItems, shippingAddress, invoiceAddress, paymentMethod, shippingMethod },
                    this.$auth.getToken('keycloak')
                )
            } catch (err) {
                this.error = err.message || 'Leider gab es ein Problem. Bitte später erneut versuchen.'
            }
        },
        getAddress(addressType) {
            return {
                firstName: this.order[`${addressType}Address`].firstName,
                lastName: this.order[`${addressType}Address`].lastName,
                address: `${this.order[`${addressType}Address`].street} ${this.order[`${addressType}Address`].number}`,
                additionalAddress: this.order[`${addressType}Address`].additionalAddress,
                zip: this.order[`${addressType}Address`].zip,
                city: this.order[`${addressType}Address`].city,
                country: this.order[`${addressType}Address`].country,
            }
        },
    },
}
</script>

<style scoped></style>