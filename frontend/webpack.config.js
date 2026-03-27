const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

const TerserPlugin = require("terser-webpack-plugin");

module.exports = {
    plugins: [
        new BundleAnalyzerPlugin()
    ],
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin({
            terserOptions: {
                compress: { drop_console: true },
            },
        })],
    },
};
