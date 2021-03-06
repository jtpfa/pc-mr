/**
 * @module DateFormatter
 * @author Jonas Pfannkuche
 */

class DateFormatter {
    /**
     * Formats the a timestamp to the loacale date format:
     * DD.MM.YYYY, HH:MI:SS
     * @static
     * @param {Number} timestamp
     * @returns {String}
     */
    static toDateString(timestamp) {
        const date = new Date(timestamp)

        return `${date.toLocaleDateString()}, ${date.toLocaleTimeString()}`
    }
}

export default DateFormatter
