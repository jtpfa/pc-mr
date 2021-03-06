/**
 * @module ImageSrcSet
 * @author Jonas Pfannkuche
 */

class ImageSrcSet {
    /**
     * @param {String} mediaUrl - Base url to the product images folder
     */
    constructor(mediaUrl) {
        this.baseUrl = mediaUrl
    }

    /**
     * Generates source set of image urls of the product
     * @param {Number} productId - Product id of the requested images
     * @returns {String} Urls of the image in HTML srcSet format
     */
    getSrcSet(productId) {
        const sizes = ['256', '512', '1024']
        let result = ''

        sizes.forEach((size, index) => {
            result += `${this.getImageUrl(productId, size)} ${index + 1}x, `
        })

        // remove last colon and whitespace from result
        return result.slice(0, -2)
    }

    /**
     * Get the product image for one resolution
     * @param {Number} productId - Product id of the requested image
     * @param {String} [size='512'] - Width of the image
     * @returns {String} Url of the image
     */
    getImageUrl(productId, size = '512') {
        return `${this.baseUrl}${productId}_${size}.png`
    }
}

export default ImageSrcSet
