const path = require('path');
const { getDefaultConfig } = require('@react-native/metro-config');

const root = path.resolve(__dirname, '..');
const exampleNodeModules = path.resolve(__dirname, 'node_modules');
const rootNodeModules = path.resolve(root, 'node_modules');

const config = getDefaultConfig(__dirname);

config.watchFolders = [root];

config.resolver.nodeModulesPath = null;

config.resolver.nodeModulesPaths = [exampleNodeModules, rootNodeModules];

config.resolver.unstable_enablePackageExports = true;

const escapeRegExp = (s) => s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

config.resolver.extraNodeModules = {
  ...config.resolver.extraNodeModules,
  'react': path.join(exampleNodeModules, 'react'),
  'react-native': path.join(exampleNodeModules, 'react-native'),
};

config.resolver.blockList = [
  ...[config.resolver.blockList].flat().filter(Boolean),
  new RegExp(
    `^${escapeRegExp(path.join(root, 'node_modules/react-native'))}[\\\\/]`
  ),
  new RegExp(`^${escapeRegExp(path.join(root, 'node_modules/react'))}[\\\\/]`),
];

module.exports = config;
