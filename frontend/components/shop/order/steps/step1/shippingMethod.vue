<template>
    <div>
        <h2>
            Versandart wählen
            <span class="mandatory">*</span>
        </h2>
        <b-list-group>
            <b-list-group-item v-for="method in availableMethods" :key="method.id">
                <b-form-radio
                    v-model="selectedMethod"
                    name="shipping-methods"
                    required
                    :state="validState"
                    :value="{ id: method.id, name: method.name, description: method.description }"
                >
                    <span class="shipping-key">{{ method.description }} – {{ method.name }}</span>
                    <br />
                    <span class="text-muted">{{ method.shippingTime }}</span>
                </b-form-radio>
            </b-list-group-item>
        </b-list-group>
    </div>
</template>

<script>
/**
 * @component OrderShippingMethod
 * @desc Radio group of all shipping methods
 * @author Jonas Pfannkuche
 */

export default {
    name: 'OrderShippingMethod',
    data() {
        return {
            /**
             * @member {Boolean|Function} [validState='null'] - Set to null to prevent validation when user navigates between steps
             */
            validState: null,
            /**
             * @member {Array.<{id: String, name: String, description: String, shippingTime: String}>} availableMethods - Available shipping methods
             */
            availableMethods: [
                {
                    id: 'DEFAULT',
                    name: 'UPS',
                    description: 'Standardversand',
                    shippingTime: 'Lieferung innerhalb von 2-4 Werktagen',
                },
                {
                    id: 'EXPRESS',
                    name: 'DHL',
                    description: 'Expressversand',
                    shippingTime: 'Lieferung innerhalb von 1-2 Werktagen',
                },
            ],
        }
    },
    computed: {
        /**
         * @computed {Object} selectedMethod - Selected shipping method
         */
        selectedMethod: {
            get() {
                return this.$store.state.order.shippingMethod
            },
            set(shippingMethod) {
                this.$store.commit('order/updateOrderInformation', { key: 'shippingMethod', data: shippingMethod })
            },
        },
    },
}
</script>

<style lang="scss" scoped>
.shipping-key {
    font-size: 1.2rem;
}

span.mandatory {
    color: $danger;
}
</style>
