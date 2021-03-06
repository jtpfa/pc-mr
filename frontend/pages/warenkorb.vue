<template>
    <client-only>
        <main class="container">
            <template v-if="cartCountElements > 0">
                <div class="row my-5">
                    <div class="col-md-7 mb-5 mb-md-0">
                        <cart-products />
                    </div>
                    <div class="col-md-5">
                        <cart-summary :error="error">
                            <b-button class="my-3 w-100" size="lg" variant="primary" @click="onSubmit">
                                {{ cartChanged ? 'Warenkorb geprüft? Ab zur Kasse' : 'Zur Kasse gehen' }}
                            </b-button>
                            <b-button class="w-100" to="/" variant="white">Weiter einkaufen</b-button>
                        </cart-summary>
                    </div>
                </div>
            </template>
            <template v-else>
                <b-jumbotron
                    bg-variant="light"
                    class="my-5"
                    header="Warenkorb ist leer"
                    :header-level="4"
                ></b-jumbotron>
            </template>
        </main>
    </client-only>
</template>

<script>
/**
 * @component ShoppingCartPage
 * @desc Shopping cart page
 * @author Jonas Pfannkuche
 */
import { mapGetters } from 'vuex'
import CartProducts from '~/components/shop/cart/products'
import CartSummary from '~/components/shop/cart/summary'

export default {
    components: { CartSummary, CartProducts },
    data() {
        return {
            /**
             * @member {String} error - General error message
             */
            error: '',
            /**
             * @member {Boolean} cartChanged - Element of cart changed
             */
            cartChanged: false,
        }
    },
    computed: {
        /**
         * @computed {Number} cartCountElements - Number of products in the cart
         */
        ...mapGetters({ cartCountElements: 'shoppingcart/cartCountElements' }),
        /**
         * @computed {Object} cart - Products in cart
         */
        cart() {
            return this.$store.state.shoppingcart.cart
        },
        /**
         * @computed {Boolean} stockOfElementChanged - Element of cart changed
         */
        stockOfElementChanged() {
            return this.$store.state.shoppingcart.stockOfElementChanged
        },
    },
    methods: {
        /**
         * @method onSubmit
         * @desc Checks if the products and their quantity are still in stock and shows individual message if something changed
         * @returns {Promise<void>}
         */
        async onSubmit() {
            this.cartChanged = false

            const results = this.cart.map(item => {
                return new Promise((resolve, reject) => {
                    try {
                        this.$api
                            .getProduct(item.id)
                            .then(product => {
                                this.$store.commit('shoppingcart/updateCart', product)

                                if (this.stockOfElementChanged) {
                                    this.cartChanged = true

                                    if (product.stock === 0) {
                                        this.$store.commit('shoppingcart/removeAllFromCart', item)
                                        this.generateToastMessage(
                                            'Es gab eine Änderung des Warenkorbs',
                                            `Wir mussten leider "${item.name}" aus deinem Warenkorb entfernen, da dieser Artikel nicht mehr verfügbar ist.`,
                                            'danger'
                                        )
                                    } else {
                                        this.generateToastMessage(
                                            'Es gab eine Änderung beim Lagerbestand',
                                            `Wir mussten leider bei "${item.name}" deine gewünschte Menge auf unseren neuen Lagerstand aktualisieren. Bitte den Warenkorb überprüfen und erneut absenden.`,
                                            'warning'
                                        )
                                    }
                                }
                            })
                            .then(() => resolve())
                            .catch(err => {
                                if (err.message.toLocaleLowerCase().includes('kein artikel')) {
                                    this.cartChanged = true
                                    this.$store.commit('shoppingcart/removeAllFromCart', item)
                                    this.generateToastMessage(
                                        'Es gab eine Änderung des Warenkorbs',
                                        `Wir mussten leider "${item.name}" aus deinem Warenkorb entfernen, da dieser Artikel nicht mehr verfügbar ist.`,
                                        'danger'
                                    )
                                    reject()
                                } else {
                                    this.error =
                                        err.message ||
                                        'Bitte Seite neu laden. Es gibt ein Problem mit mind. einem der Produkte.'
                                    reject()
                                }
                            })
                    } catch (err) {
                        this.error =
                            err.message || 'Bitte Seite neu laden. Es gibt ein Problem mit mind. einem der Produkte.'
                        reject()
                    }
                })
            })

            await Promise.allSettled(results).then(() => {
                if (!this.cartChanged && !results.every(promise => promise === 'fulfilled')) {
                    this.$router.push({ path: '/bestellung', query: { step: 1 } })
                }
            })
        },
        /**
         * Generates a bootstrap toast (pop-up)
         * @param {String} title - Title of the message
         * @param {String} description - Text of the message
         * @param {String} variant - Bootstrap theme color
         */
        generateToastMessage(title, description, variant) {
            this.$bvToast.toast(description, {
                title,
                noAutoHide: true,
                appendToast: true,
                isStatus: true,
                solid: true,
                variant,
            })
        },
    },
    head() {
        return {
            title: `${this.$route.name.replace(/^\w/, c => c.toUpperCase())} – PC Masterrace`,
        }
    },
}
</script>

<style scoped></style>
