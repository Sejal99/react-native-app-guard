import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  isDeviceRooted(): boolean;
  isScreenRecording(): boolean;
  setScreenshotBlocked(blocked: boolean): void;
  isEmulator(): boolean;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AppGuard');
