<template>
    <b-card>
        <template v-slot:header>
            <div class="d-flex justify-content-between align-items-center">
                <h4 class="mb-0 card-title">{{ title }}</h4>
                <b-button variant="white" @click="step = stepToEditData"><icon-pen class="edit" /></b-button>
            </div>
        </template>

        <slot />
    </b-card>
</template>

<script>
/**
 * @component OrderOverviewCard
 * @desc General card template for order overview with ability to edit data (jumps to the given step)
 * @author Jonas Pfannkuche
 */

import IconPen from '~/components/general/icons/pen'

export default {
    name: 'OrderOverviewCard',
    components: { IconPen },
    props: {
        /**
         * @vprop {String} title - Titel/description of the information in the card
         */
        title: {
            type: String,
            required: true,
        },
        /**
         * @vprop {Number} stepToEditData - Step where data is editable
         */
        stepToEditData: {
            type: Number,
            required: true,
        },
    },
    computed: {
        /**
         * @computed {Number} step - Current step of order process
         */
        step: {
            get() {
                return this.$store.state.order.step
            },
            set(step) {
                this.$store.commit('order/updateOrderInformation', { key: 'step', data: step })
            },
        },
    },
}
</script>

<style lang="scss" scoped>
.card-header {
    padding-right: 0.25rem;

    svg.edit {
        min-width: 1rem;
        width: 1rem;
        fill: $primary;
        transition: 0.3s opacity ease-in-out;
    }

    button:hover svg {
        opacity: 0.6;
    }
}
</style>
