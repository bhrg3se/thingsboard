///
/// Copyright © 2016-2023 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { isDefinedAndNotNull } from '@core/utils';
import {
  phoneNumberPatternSparrow,
  SmsProviderConfiguration,
  SmsProviderType,
  SparrowSmsProviderConfiguration
} from '@shared/models/settings.models';

@Component({
  selector: 'tb-sparrow-sms-provider-configuration',
  templateUrl: './sparrow-sms-provider-configuration.component.html',
  styleUrls: [],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => SparrowSmsProviderConfigurationComponent),
    multi: true
  }]
})
export class SparrowSmsProviderConfigurationComponent implements ControlValueAccessor, OnInit {

  sparrowSmsProviderConfigurationFormGroup: UntypedFormGroup;

  

  private requiredValue: boolean;

  get required(): boolean {
    return this.requiredValue;
  }

  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input()
  disabled: boolean;

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private fb: UntypedFormBuilder) {
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  ngOnInit() {
    this.sparrowSmsProviderConfigurationFormGroup = this.fb.group({
      numberFrom: [null, [Validators.required]],
      token: [null, Validators.required]
    });
    this.sparrowSmsProviderConfigurationFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.sparrowSmsProviderConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.sparrowSmsProviderConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: SparrowSmsProviderConfiguration | null): void {
    if (isDefinedAndNotNull(value)) {
      this.sparrowSmsProviderConfigurationFormGroup.patchValue(value, {emitEvent: false});
    }
  }

  private updateModel() {
    let configuration: SparrowSmsProviderConfiguration = null;
    if (this.sparrowSmsProviderConfigurationFormGroup.valid) {
      configuration = this.sparrowSmsProviderConfigurationFormGroup.value;
      (configuration as SmsProviderConfiguration).type = SmsProviderType.SPARROW;
    }
    this.propagateChange(configuration);
  }
}
