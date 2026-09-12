import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  isDeviceRooted(): boolean;
  isScreenRecording(): boolean;
  setScreenshotBlocked(blocked: boolean): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AppGuard');
