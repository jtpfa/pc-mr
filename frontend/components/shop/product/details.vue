<template>
    <div>
        <div class="row my-5">
            <div class="col-12 col-md-5 d-flex justify-content-center align-items-center mb-5 mb-md-0">
                <div class="lazy-image d-flex justify-content-center align-items-center">
                    <b-img-lazy
                        :alt="product.name"
                        onerror="this.onerror=null;this.srcset='/img/logo-placeholder.svg';"
                        :src="$imageSrcSet.getImageUrl(product.id, 512)"
                        :srcset="$imageSrcSet.getSrcSet(product.id)"
                        :title="product.name"
                    />
                    <spinner />
                </div>
            </div>

            <div class="col-12 col-md-7">
                <h1 class="text-primary text-center text-md-left oblique mb-0">{{ product.name }}</h1>
                <span class="shipping-time d-block text-center text-md-left text-muted">{{ shippingInformation }}</span>

                <p class="mt-5">
                    {{ product.description }}
                </p>

                <div class="d-flex justify-content-between align-items-end flex-column flex-sm-row mt-5">
                    <div class="price-information mb-2 mb-sm-0">
                        <span class="price big-noodle oblique text-dark">
                            {{ $currencyConverter.convertCentsToEuro(product.price) }}
                        </span>
                        <span class="text-muted">inkl. MwSt.</span>
                        <span class="text-muted">keine Versandkosten</span>
                    </div>

                    <add-to-cart :orderable="orderable" :product="product" />
                </div>
            </div>
        </div>
        <div>
            <p class="h5 my-5">Produktdetails</p>

            <div v-html="product.details"></div>
        </div>
    </div>
</template>

<script>
/**
 * @component ProductDetails
 * @desc Detailed view of product
 * @author Jonas Pfannkuche
 */

import { mapGetters } from 'vuex'
import AddToCart from '~/components/shop/cart/addToCart'
import Spinner from '~/components/shop/layout/spinner'

export default {
    name: 'ProductDetails',
    components: { Spinner, AddToCart },
    props: {
        /**
         * @vprop {Object} product - Product with information that should be rendered
         */
        product: {
            type: Object,
            required: true,
        },
    },
    computed: {
        /**
         * @computed {Number} productQuantity - Quantity of product in shopping cart
         */
        ...mapGetters({ productQuantity: 'shoppingcart/productQuantity' }),
        /**
         * @computed {String} shippingInformation - Shipping information text
         */
        shippingInformation() {
            return !this.orderable ? 'Nicht mehr auf Lager' : 'Lieferbar in 1-2 Werktagen'
        },
        /**
         * @computed {Boolean} orderable - Product order availability
         */
        orderable() {
            return this.product.stock > 0 && this.productQuantity(this.product.id) < this.product.stock
        },
    },
}
</script>

<style lang="scss" scoped>
.lazy-image {
    position: relative;
    flex-shrink: 0;
    width: 100%;
    min-height: 8rem;
    max-height: 100%;
    overflow: hidden;

    img {
        width: 50%;
    }
}
.shipping-time {
    margin-top: -0.5rem;
}

.price-information {
    span {
        display: block;
        line-height: 1;
    }

    .price {
        font-size: 2.5rem;
    }

    span:not(.price) {
        font-size: 0.8rem;
    }
}
</style>
