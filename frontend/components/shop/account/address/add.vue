<template>
    <b-form ref="form" novalidate @submit.prevent="onSubmit">
        <b-card>
            <div class="row">
                <div class="mb-4 col-xl-6" role="group">
                    <label for="newFirstName">
                        Vorname
                        <span class="mandatory">*</span>
                    </label>
                    <b-form-input
                        id="newFirstName"
                        v-model="firstName"
                        aria-describedby="input-live-feedback"
                        autocomplete="given-name"
                        pattern="^.{1,50}$"
                        placeholder="Vorname"
                        required
                        trim
                    />

                    <b-form-invalid-feedback id="input-live-feedback">
                        Bitte gib deinen Vornamen an.
                    </b-form-invalid-feedback>
                </div>

                <div class="mb-4 col-xl-6" role="group">
                    <label for="newLastName">
                        Nachname
                        <span class="mandatory">*</span>
                    </label>
                    <b-form-input
                        id="newLastName"
                        v-model="lastName"
                        aria-describedby="input-live-feedback"
                        autocomplete="family-name"
                        pattern="^.{1,50}$"
                        placeholder="Nachname"
                        required
                        trim
                    />

                    <b-form-invalid-feedback id="input-live-feedback">
                        Bitte gib deinen Nachnamen an.
                    </b-form-invalid-feedback>
                </div>
            </div>

            <div class="row">
                <div class="mb-4 col-sm-4" role="group">
                    <label for="newZip">
                        PLZ
                        <span class="mandatory">*</span>
                    </label>
                    <b-form-input
                        id="newZip"
                        v-model="zip"
                        aria-describedby="input-live-feedback"
                        pattern="^.{1,50}$"
                        placeholder="PLZ"
                        required
                        trim
                    />

                    <b-form-invalid-feedback id="input-live-feedback">Bitte gib eine PLZ an.</b-form-invalid-feedback>
                </div>

                <div class="mb-4 col-sm-8" role="group">
                    <label for="newCity">
                        Stadt
                        <span class="mandatory">*</span>
                    </label>
                    <b-form-input
                        id="newCity"
                        v-model="city"
                        aria-describedby="input-live-feedback"
                        pattern="^.{1,100}$"
                        placeholder="Stadt"
                        required
                        trim
                    />
                </div>

                <b-form-invalid-feedback id="input-live-feedback">Bitte gib eine Stadt an.</b-form-invalid-feedback>
            </div>

            <div class="row">
                <div class="mb-4 col-12" role="group">
                    <label for="newAddress">
                        Straße und Hausnr.
                        <span class="mandatory">*</span>
                    </label>
                    <b-form-input
                        id="newAddress"
                        v-model="address"
                        aria-describedby="input-live-feedback"
                        pattern="^.{1,255}$"
                        placeholder="Straße und Hausnr."
                        required
                        trim
                    />

                    <b-form-invalid-feedback id="input-live-feedback">
                        Bitte gib eine Straße inkl. Hausnr. an.
                    </b-form-invalid-feedback>
                </div>
            </div>

            <div class="row">
                <div class="mb-4 col-sm-4 col-xl-3" role="group">
                    <label for="newAdditionalAddress">Adresszusatz</label>
                    <b-form-input
                        id="newAdditionalAddress"
                        v-model="additionalAddress"
                        aria-describedby="input-live-feedback"
                        pattern="^.{1,255}$"
                        placeholder="Zusatz"
                        trim
                    />

                    <b-form-invalid-feedback id="input-live-feedback">
                        Bitte gib einen Adresszusatz an.
                    </b-form-invalid-feedback>
                </div>

                <div class="mb-4 col-sm-8 col-xl-9" role="group">
                    <label for="newCountry">
                        Land
                        <span class="mandatory">*</span>
                    </label>
                    <b-form-input
                        id="newCountry"
                        v-model="country"
                        aria-describedby="input-live-feedback"
                        pattern="^.{1,50}$"
                        placeholder="Land"
                        required
                        trim
                    />

                    <b-form-invalid-feedback id="input-live-feedback">Bitte gib ein Land an.</b-form-invalid-feedback>
                </div>
            </div>

            <b-alert class="mt-3" :show="error.length > 0" variant="danger" v-html="error" />

            <button-container :loading="loading" text="Adresse hinzufügen" />
        </b-card>
    </b-form>
</template>

<script>
/**
 * @component AccountAddAddress
 * @desc Form to add a new address to account
 * @author Jonas Pfannkuche
 */

import ButtonContainer from '~/components/general/layout/buttonContainer'

export default {
    name: 'AccountAddAddress',
    components: { ButtonContainer },
    data() {
        return {
            /**
             * @member {String} firstName - First name of the user (get from logged in user)
             */
            firstName: this.$auth.user.firstName,
            /**
             * @member {String} lastName - Last name of the user (get from logged in user)
             */
            lastName: this.$auth.user.lastName,
            /**
             * @member {String} zip - Zip of the address
             */
            zip: '',
            /**
             * @member {String} city - City of the address
             */
            city: '',
            /**
             * @member {String} address - Street and house number of the address
             */
            address: '',
            /**
             * @member {String} additionalAddress - Additional address of the address
             */
            additionalAddress: '',
            /**
             * @member {String} country - Country of the address
             */
            country: '',
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
    methods: {
        /**
         * @method addAddress
         * @desc Calls api endpoint to add new address and handles response
         * @returns {Promise<void>}
         */
        async addAddress() {
            try {
                await this.$api.addCustomerAddress(
                    {
                        firstName: this.firstName,
                        lastName: this.lastName,
                        address: this.address,
                        additionalAddress: this.additionalAddress,
                        zip: this.zip,
                        city: this.city,
                        country: this.country,
                    },
                    this.$auth.getToken('keycloak')
                )
                this.clearForm()
                this.$root.$emit('bv::toggle::collapse', 'new-address')
                this.$router.app.refresh()
            } catch (err) {
                this.error = err.message || 'Leider gab es ein Problem. Bitte später erneut versuchen.'
            }
        },
        /**
         * @method onSubmit
         * @desc Validates the form, shows validation state and calls {@link component:AccountAddAddress~addAddress addAddress} if the form is valid
         * @param {Object} event - Browser event which is fired on submitting the form
         */
        async onSubmit(event) {
            this.loading = true
            this.error = ''
            if (!this.$refs.form.checkValidity()) {
                this.$refs.form.classList.add('was-validated')
                event.preventDefault()
                event.stopPropagation()
            } else {
                this.$refs.form.classList.add('was-validated')
                await this.addAddress()
            }
            this.loading = false
        },
        /**
         * @method clearForm
         * @desc Clears all input fields and removes validation state of form
         */
        clearForm() {
            this.$refs.form.classList.remove('was-validated')
            this.firstName = ''
            this.lastName = ''
            this.zip = ''
            this.city = ''
            this.address = ''
            this.additionalAddress = ''
            this.country = ''
        },
    },
}
</script>

<style scoped></style>
