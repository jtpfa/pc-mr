<template>
    <b-modal
        :id="modalId"
        centered
        hide-footer
        :hide-header-close="loading"
        :no-close-on-backdrop="loading"
        :no-close-on-esc="loading"
        no-enforce-focus
        scrollable
        title="Produktdaten bearbeiten"
    >
        <b-form ref="form" novalidate @submit.prevent="onSubmit">
            <div class="mb-4" role="group">
                <label for="name">
                    Artikelbezeichnung
                    <span class="mandatory">*</span>
                </label>
                <b-form-input
                    id="name"
                    v-model="product.name"
                    aria-describedby="input-live-feedback"
                    pattern="^.{4,255}$"
                    placeholder="Artikelbezeichnung"
                    required
                    trim
                />

                <b-form-invalid-feedback id="input-live-feedback">
                    Bitte Artikelbezeichnung angeben.
                </b-form-invalid-feedback>
            </div>
            <div class="mb-4" role="group">
                <label for="description">
                    Beschreibung
                    <span class="mandatory">*</span>
                </label>
                <b-form-input
                    id="description"
                    v-model="product.description"
                    aria-describedby="input-live-feedback"
                    pattern="^.{4,4096}$"
                    placeholder="Beschreibung"
                    required
                    trim
                />

                <b-form-invalid-feedback id="input-live-feedback">Bitte Beschreibung angeben.</b-form-invalid-feedback>
            </div>

            <form-field-editor ref="editorDetails" :current-details="product.details" />

            <div class="mb-4" role="group">
                <label for="price">
                    Preis
                    <span class="mandatory">*</span>
                </label>
                <b-input-group id="price" prepend="Euro und Cent">
                    <b-form-input
                        v-model="euro"
                        aria-describedby="input-live-feedback"
                        :formatter="checkEuroRange"
                        max="999999"
                        min="0"
                        required
                        trim
                        type="number"
                    />
                    <b-input-group-text>,</b-input-group-text>
                    <b-form-input
                        v-model="ct"
                        aria-describedby="input-live-feedback"
                        :formatter="checkCtRange"
                        max="99"
                        min="0"
                        required
                        trim
                        type="number"
                    />
                    <b-input-group-append is-text>€</b-input-group-append>
                </b-input-group>

                <b-form-invalid-feedback id="input-live-feedback">Preis angeben.</b-form-invalid-feedback>
            </div>

            <div class="mb-4" role="group">
                <label for="stock">
                    Lagerbestand
                    <span class="mandatory">*</span>
                </label>
                <b-form-input
                    id="stock"
                    v-model="product.stock"
                    aria-describedby="input-live-feedback"
                    min="0"
                    placeholder="Menge"
                    required
                    trim
                    type="number"
                />

                <b-form-invalid-feedback id="input-live-feedback">Bitte Bestand angeben.</b-form-invalid-feedback>
            </div>

            <form-field-file-upload ref="fileInput" :replace="true" />

            <b-alert class="my-3" :show="error.length > 0" variant="danger" v-html="error" />

            <button-container :loading="loading" text="Produkt speichern" />
        </b-form>
    </b-modal>
</template>

<script>
/**
 * @component ProductEdit
 * @desc Form to edit an existing product inside of a modal. Modals er rendered by default on client side.
 * @author Jonas Pfannkuche
 */

import FormFieldEditor from '~/components/dashboard/form-fields/editor'
import FormFieldFileUpload from '~/components/dashboard/form-fields/fileUpload'
import ButtonContainer from '~/components/general/layout/buttonContainer'

export default {
    name: 'ProductEdit',
    components: { ButtonContainer, FormFieldEditor, FormFieldFileUpload },
    props: {
        /**
         * @vprop {Object} product - Product which should be edited
         */
        product: {
            type: Object,
            required: true,
        },
        /**
         * @vprop {String} modalId - Id of the modal to identify it in root scope
         */
        modalId: {
            type: String,
            default: '',
        },
    },
    data() {
        return {
            /**
             * @member {String} priceEur - Euros of product price
             */
            priceEur: this.getEuro(this.product.price),
            /**
             * @member {Number} priceCt - Cents of product price
             */
            priceCt: this.getCt(this.product.price),
            /**
             * @member {String} error - General error message
             */
            error: '',
            /**
             * @member {Boolean} loading - Request status
             */
            loading: false,
        }
    },
    computed: {
        /**
         * @computed {String|Number} euro - Synchronized euros input field value
         */
        euro: {
            get() {
                // If euros of price exists use it otherwise get the euros of our current product
                return this.priceEur !== undefined ? this.priceEur : this.getEuro(this.product.price)
            },
            set(newEuroPrice) {
                this.priceEur = newEuroPrice
            },
        },
        /**
         * @computed {String|Number} ct - Synchronized cents input field value
         */
        ct: {
            get() {
                // If cents of price exists use it otherwise get the cents of our current product
                return this.priceCt !== undefined ? this.priceCt : this.getCt(this.product.price)
            },
            set(newCtPrice) {
                this.priceCt = newCtPrice
            },
        },
    },
    methods: {
        /**
         * @method checkEuroRange
         * @desc Checks if entered euro value are between 0 and 999999
         * @param {String} value - Current euro value of input field
         * @returns {String}
         */
        checkEuroRange(value) {
            if (+value < 0) {
                return '0'
            }
            if (+value > 999999) {
                return '999999'
            }
            return value
        },
        /**
         * @method checkCtRange
         * @desc Checks if entered cent value is between 0 and 99
         * @param {String} value - Current cent value of input field
         * @returns {String}
         */
        checkCtRange(value) {
            if (+value < 0) {
                return '0'
            }
            if (+value > 99) {
                return '99'
            }
            return value
        },
        /**
         * @method getEuro
         * @desc Returns the euros of the given price
         * @param {Number} price - Product price
         * @returns {String|Number}
         */
        getEuro(price) {
            if (price !== undefined) {
                const priceString = price.toString()
                let eur = priceString.slice(0, priceString?.length - 2)

                if (eur.length === 0) {
                    eur = 0
                }

                return eur
            }

            return -1
        },
        /**
         * @method getCt
         * @desc Returns the cents of the given price
         * @param {Number} price - Product price
         * @returns {String|Number}
         */
        getCt(price) {
            if (price !== undefined) {
                return price.toString().slice(-2)
            }

            return -1
        },
        /**
         * @method editProduct
         * @desc Calls api endpoint to edit product and handles response
         * @returns {Promise<void>}
         */
        async editProduct() {
            try {
                await this.$api.editProduct(
                    {
                        id: this.product.id,
                        name: this.product.name,
                        description: this.product.description,
                        details: this.$refs.editorDetails.details,
                        price: +(this.euro + (+this.ct !== 0 && +this.ct <= 9 ? `0${+this.ct}` : +this.ct)),
                        stock: +this.product.stock,
                    },
                    this.$refs.fileInput.image,
                    this.$auth.getToken('keycloak')
                )

                this.$root.$emit('bv::hide::modal', this.modalId)
                this.$router.app.refresh()
            } catch (err) {
                this.error = err.message || 'Leider gab es ein Problem. Bitte später erneut versuchen.'
            }
        },
        /**
         * @method onSubmit
         * @desc Validates the form, shows validation state and calls {@link component:ProductEdit~editProduct editProduct} if the form is valid
         * @param {Object} event - Browser event which is fired on submitting the form
         */
        async onSubmit(event) {
            this.loading = true
            this.error = ''
            if (!this.$refs.form.checkValidity() || !this.$refs.editorDetails.isValid()) {
                this.$refs.form.classList.add('was-validated')
                event.preventDefault()
            } else {
                this.$refs.form.classList.add('was-validated')
                await this.editProduct()
            }
            this.loading = false
        },
    },
}
</script>

<style scoped></style>
