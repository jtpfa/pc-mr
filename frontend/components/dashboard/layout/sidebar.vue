<template>
    <b-sidebar no-close-on-route-change no-header shadow visible>
        <div class="sidebar-content py-2">
            <nav class="px-1 mb-3">
                <b-nav vertical>
                    <b-nav-item to="/dashboard"><icon-dashboard /></b-nav-item>
                    <b-nav-item to="/dashboard/produkte"><icon-barcode /></b-nav-item>
                    <b-nav-item to="/dashboard/nutzer"><icon-users /></b-nav-item>
                    <b-nav-item to="/dashboard/bestellungen"><icon-orders /></b-nav-item>
                </b-nav>
            </nav>
        </div>
    </b-sidebar>
</template>

<script>
/**
 * @component DashboardSidebar
 * @desc Sidebar of dashboard
 * @author Jonas Pfannkuche
 */

import IconBarcode from '~/components/general/icons/barcode'
import IconUsers from '~/components/general/icons/users'
import IconDashboard from '~/components/general/icons/dashboard'
import IconOrders from '~/components/general/icons/orders'

export default {
    name: 'DashboardSidebar',
    components: { IconDashboard, IconBarcode, IconUsers, IconOrders },
}
</script>

<style lang="scss" scoped>
// Override default bootstrap sidebar position
.b-sidebar-outer,
::v-deep .b-sidebar {
    position: relative !important;
}

.b-sidebar-outer {
    height: unset;
    min-height: 100vh;
}

::v-deep .b-sidebar {
    height: 100% !important;
    width: 5.875rem !important;
}

.nav-item {
    background-color: $white;
    border-radius: 0.25rem;

    &:not(:last-of-type) {
        margin-bottom: 0.25rem;
    }

    &:hover {
        svg {
            fill: $primary;
        }
    }
}

.nav-link {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 4rem;

    svg {
        width: 2.5rem;
        height: auto;
        fill: $gray-400;
        transition: 0.3s fill ease-in-out;
    }

    &.nuxt-link-exact-active svg {
        fill: $primary;
    }
}
</style>
