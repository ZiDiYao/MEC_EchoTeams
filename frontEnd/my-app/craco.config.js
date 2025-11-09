const path = require("path");

module.exports = {
  webpack: {
    configure: (webpackConfig) => {
      // support WebAssembly module
      webpackConfig.experiments = {
        asyncWebAssembly: true,
        topLevelAwait: true,
      };

      // prevent @xenova/transformers wrongly packing
      webpackConfig.resolve.fallback = {
        fs: false,
        path: false,
        crypto: false,
      };

      return webpackConfig;
    },
  },
};
