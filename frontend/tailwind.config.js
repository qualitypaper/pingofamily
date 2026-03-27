/** @type {import('tailwindcss').Config} */

module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx,json}",
    "./node_modules/react-tailwindcss-datepicker/dist/index.esm.js"
  ],
  darkMode: ["class", '[data-theme="dark"]'],
  theme: {
    extend: {
      colors: {
        'color-big-text': '#558FFB',
        'button-color': '#1E90FF',
        'color-text': '#0A3D62',
        'color-light': '#CCFFFF',
      },
      boxShadow: {
        'custom-blue': '0px 5px 15px rgba(15, 60, 255, 0.070)',
      },
    },
  },
  plugins: [require("@tailwindcss/typography")],

}
