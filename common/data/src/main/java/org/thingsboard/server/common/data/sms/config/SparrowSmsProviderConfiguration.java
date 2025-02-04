/**
 * Copyright © 2016-2023 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.sms.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class SparrowSmsProviderConfiguration implements SmsProviderConfiguration {

    @ApiModelProperty(position = 1, value = "Sparrow SMS Token.")
    private String token;
    @ApiModelProperty(position = 2, value = "The number/id of a sender.")
    private String numberFrom;

    @Override
    public SmsProviderType getType() {
        return SmsProviderType.SPARROW;
    }

}
