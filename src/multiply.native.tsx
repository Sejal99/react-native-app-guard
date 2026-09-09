import AppGuard from './NativeAppGuard';

export function multiply(a: number, b: number): number {
  return AppGuard.multiply(a, b);
}
