<template>
    <div>
        <b-alert :show="fetchErrorMsg.length > 0" variant="warning" v-html="fetchErrorMsg" />
        <b-alert :show="error.length > 0" variant="danger" v-html="error" />

        <b-table
            :id="`${type}-table`"
            :busy="$fetchState.pending"
            :current-page="currentPage"
            :fields="fields"
            head-variant="primary"
            hover
            :items="items"
            outlined
            :per-page="perPage"
            primary-key="id"
            :responsive="true"
            show-empty
            :sort-by="sortBy"
            :sort-desc="sortDesc"
        >
            <template v-slot:cell(image)="row">
                <b-img-lazy
                    :alt="row.item.name"
                    class="image-preview"
                    onerror="this.onerror=null;this.srcset='/img/logo-placeholder.svg';"
                    :src="$imageSrcSet.getImageUrl(row.item.id, 256)"
                    :title="row.item.name"
                />
            </template>

            <template v-if="!dashboard" v-slot:cell(actions)="row">
                <b-button-group class="float-right">
                    <b-button
                        v-if="type !== 'order' && isUserAllowedToEdit"
                        class="action-button"
                        size="sm"
                        variant="primary"
                        @click="showEditModal(row.item.id ? row.item.id : row.item.email)"
                    >
                        <icon-pen />
                    </b-button>
                    <b-button
                        v-if="type !== 'order' && isUserAllowedToDelete"
                        class="action-button"
                        size="sm"
                        variant="danger"
                        @click="confirmDeletion(row.item)"
                    >
                        <icon-trash />
                    </b-button>
                </b-button-group>

                <user-edit
                    v-if="['customer', 'employee', 'admin'].includes(type)"
                    :modal-id="`modal-edit-${type}-${row.item.email}`"
                    :role="type"
                    :user="Object.assign({}, row.item)"
                />

                <product-edit
                    v-else-if="type === 'product'"
                    :modal-id="`modal-edit-${type}-${row.item.id}`"
                    :product="Object.assign({}, row.item)"
                />
            </template>

            <template v-slot:table-busy>
                <div class="text-center text-primary my-2">
                    <b-spinner class="align-middle"></b-spinner>
                    <strong>Lädt...</strong>
                </div>
            </template>

            <template v-slot:empty>
                <h4 class="text-center text-info">Keine Daten vorhanden.</h4>
            </template>
        </b-table>

        <b-pagination
            v-model="currentPage"
            align="center"
            aria-controls="data-table"
            :limit="5"
            :per-page="perPage"
            :total-rows="rows"
        ></b-pagination>
    </div>
</template>

<script>
/**
 * @component DataOverview
 * @desc Table with pagination and sortable columns for the dashboard
 * @lifecycle mounted - Check if user has privileges to edit and/ or delete data
 * @lifecycle fetch - Fetch the data of the given type
 * @lifecycle activated - Call fetch again if last fetch more than 30 sec ago
 * @author Jonas Pfannkuche
 */

import ProductEdit from '~/components/dashboard/products/edit'
import UserEdit from '~/components/dashboard/user/edit'
import IconPen from '~/components/general/icons/pen'
import IconTrash from '~/components/general/icons/trash'

export default {
    name: 'DataOverview',
    components: { UserEdit, IconTrash, IconPen, ProductEdit },
    props: {
        /**
         * @vprop {Boolean} dashboard - Is table shown on {@link component:DashboardIndexPage main dashboard page}
         */
        dashboard: {
            type: Boolean,
            required: true,
        },
        /**
         * @vprop {Array} fields - Columns of the table
         */
        fields: {
            type: Array,
            required: true,
        },
        /**
         * @vprop {String} [sortBy=''] - Column to sort table data
         */
        sortBy: {
            type: String,
            default: '',
        },
        /**
         * @vprop {Boolean} [sortDesc=false] - Sort order
         */
        sortDesc: {
            type: Boolean,
            default: false,
        },
        /**
         * @vprop {('product'|'customer'|'employee'|'admin'|'order')} type - The allowed types of table data
         */
        type: {
            type: String,
            required: true,
            validator(type) {
                // The value must match one of these strings
                return ['product', 'customer', 'employee', 'admin', 'order'].indexOf(type) !== -1
            },
        },
    },
    async fetch() {
        try {
            switch (this.type) {
                case 'product':
                    this.items = await this.$api.getAllProductsDetailedVersion(this.$auth.getToken('keycloak'))
                    break
                case 'customer':
                    this.items = await this.$api.getCustomers(this.$auth.getToken('keycloak'))
                    break
                case 'employee':
                    this.items = await this.$api.getEmployees(this.$auth.getToken('keycloak'))
                    break
                case 'admin':
                    this.items = await this.$api.getAdmins(this.$auth.getToken('keycloak'))
                    break
                case 'order':
                    this.items = await this.$api.getAllOrders(this.$auth.getToken('keycloak'))
                    break
                default:
                    this.items = []
                    break
            }
        } catch (err) {
            this.fetchErrorMsg =
                err.message || 'Leider gab es ein Problem beim Laden der Daten. Bitte später erneut versuchen.'
        }
    },
    data() {
        return {
            /**
             * @member {Boolean} isUserAllowedToEdit - Has user privileges to edit data
             */
            isUserAllowedToEdit: false,
            /**
             * @member {Boolean} isUserAllowedToDelete - Has user privileges to delete data
             */
            isUserAllowedToDelete: false,
            /**
             * @member {Number} perPage - Maximum number of elements in table page
             */
            perPage: 5,
            /**
             * @member {Number} currentPage - Selected page of table
             */
            currentPage: 1,
            /**
             * @member {Array} items - Fetched table items
             */
            items: [],
            /**
             * @member {String} fetchErrorMsg - Error message of rejected fetch
             */
            fetchErrorMsg: '',
            /**
             * @member {String} error - General error message
             */
            error: '',
        }
    },
    computed: {
        /**
         * @computed {Number} rows - Number of table rows needed to display all items. With this we can generate the pagination.
         */
        rows() {
            return this.items.length
        },
    },
    mounted() {
        this.isUserAllowedToEdit =
            ['product', 'customer'].includes(this.type) ||
            (['employee', 'admin'].includes(this.type) && this.$auth.$state.roles?.includes('admin'))

        this.isUserAllowedToDelete =
            this.type === 'product' ||
            (['customer', 'employee', 'admin'].includes(this.type) && this.$auth.$state.roles?.includes('admin'))
    },
    activated() {
        if (this.$fetchState.timestamp <= Date.now() - 30000) {
            this.$fetch()
        }
    },
    methods: {
        /**
         * @method showEditModal
         * @desc Displays a modal with an form to edit data
         * @param {String|Number} itemId - Key to uniquely identify item
         */
        showEditModal(itemId) {
            this.$root.$emit('bv::show::modal', `modal-edit-${this.type}-${itemId}`)
        },
        /**
         * @method confirmDeletion
         * @desc Shows a confirmation modal and calls {@link component:DataOverview~deleteData deleteData} if the user confirms
         * @param {Object} item - Item which should be deleted
         */
        confirmDeletion(item) {
            const itemToDelete = { ...item }
            this.$bvModal
                .msgBoxConfirm(`Soll der Datensatz wirklich gelöscht werden?`, {
                    title: 'Löschen bestätigen',
                    size: 'sm',
                    buttonSize: 'sm',
                    okVariant: 'danger',
                    okTitle: 'JA',
                    cancelTitle: 'NEIN',
                    footerClass: 'p-2',
                    hideHeaderClose: false,
                    centered: true,
                })
                .then(async value => {
                    if (value) {
                        await this.deleteData(itemToDelete)
                    }
                })
                .catch(err => {
                    this.error = err.message || 'Leider gab es ein Problem. Bitte später erneut versuchen.'
                })
        },
        /**
         * @method deleteData
         * @desc Calls api endpoint to delete item and handles response
         * @param {Object} item - Item which should be deleted
         * @returns {Promise<void>}
         */
        async deleteData(item) {
            if (this.type === 'product') {
                try {
                    await this.$api.deleteProduct(item.id, this.$auth.getToken('keycloak'))
                    this.$router.app.refresh()
                } catch (err) {
                    this.error = err.message || 'Leider gab es ein Problem beim Löschen. Bitte später erneut versuchen.'
                }
            } else if (['customer', 'employee', 'admin'].includes(this.type)) {
                try {
                    await this.$api.deleteUser(item.email, this.type, this.$auth.getToken('keycloak'))
                    this.$router.app.refresh()
                } catch (err) {
                    this.error = err.message || 'Leider gab es ein Problem beim Löschen. Bitte später erneut versuchen.'
                }
            }
        },
    },
}
</script>

<style lang="scss" scoped>
.image-preview {
    max-width: 6rem;
    max-height: 4rem;
}

.action-button svg {
    width: 1.25rem;
    height: auto;
    fill: $white;
}

// Accept line breaks within cells
::v-deep table td {
    white-space: pre-line;
}
</style>
