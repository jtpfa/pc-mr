<template>
    <svg viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg">
        <path
            v-if="items === 0"
            d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .49.598l-1 5a.5.5 0 0 1-.465.401l-9.397.472L4.415 11H13a.5.5 0 0 1 0 1H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l.84 4.479 9.144-.459L13.89 4H3.102zM5 12a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm7 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm-7 1a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm7 0a1 1 0 1 0 0 2 1 1 0 0 0 0-2z"
        ></path>
        <path
            v-else
            d="M13.525,9l-9.4.472L4.415,11H13a.5.5,0,0,1,0,1H4a.5.5,0,0,1-.491-.408L2.01,3.607,1.61,2H.5a.5.5,0,0,1,0-1H2a.5.5,0,0,1,.485.379L2.89,3H14.5a.5.5,0,0,1,.5.5.534.534,0,0,1-.01.1l-1,5A.5.5,0,0,1,13.525,9ZM10,14a2,2,0,1,0,2-2A2,2,0,0,0,10,14ZM3,14a2,2,0,1,0,2-2A2,2,0,0,0,3,14Z"
        />
    </svg>
</template>

<script>
/**
 * @component IconShoppingCart
 * @desc Shopping cart icon; gets filled when product(s) inside
 * @author Jonas Pfannkuche
 */

export default {
    name: 'IconShoppingCart',
    props: {
        /**
         * @vprop {Number} items - Number of products in the shopping cart
         */
        items: {
            type: Number,
            default: 0,
        },
    },
}
</script>

<style scoped></style>
