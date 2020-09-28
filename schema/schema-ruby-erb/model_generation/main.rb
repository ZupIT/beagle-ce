#   Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA

#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
 
#       http://www.apache.org/licenses/LICENSE-2.0
 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

require 'erb'
require 'date'

require_relative 'Synthax/Attributes/variable.rb'
require_relative 'Synthax/Types/type.rb'

require_relative 'Models/Layout/corner_radius.rb'
require_relative 'Models/Layout/display.rb'
require_relative 'Models/Layout/edge_value.rb'
require_relative 'Models/Layout/Flex/flex.rb'
require_relative 'Models/Layout/position_type.rb'
require_relative 'Models/Layout/size.rb'
require_relative 'Models/Layout/style.rb'
require_relative 'Models/Layout/unit_value.rb'
require_relative 'Models/Layout/unit_type.rb'

require_relative 'Models/Accessibility/accessibility.rb'

require_relative 'Models/Analytics/analytics_events.rb'
require_relative 'Models/Analytics/analytics_models.rb'

require_relative 'Models/Widgets/button.rb'
require_relative 'Models/Widgets/text.rb'
require_relative 'Models/Widgets/text_input.rb'
require_relative 'Models/Widgets/text_input_type.rb'
require_relative 'Models/Widgets/container.rb'
require_relative 'Models/Widgets/image.rb'
require_relative 'Models/Widgets/image_content.rb'

require_relative 'Models/ServerDriven/Scroll/scroll_axis.rb'
require_relative 'Models/ServerDriven/Scroll/scroll_view.rb'
require_relative 'Models/ServerDriven/Lazy/lazy_component.rb'
require_relative 'Models/ServerDriven/Screen/safe_area.rb'
require_relative 'Models/ServerDriven/Screen/screen.rb'
require_relative 'Models/ServerDriven/Screen/navigation_bar.rb'
require_relative 'Models/ServerDriven/Screen/navigation_bar_item.rb'

require_relative 'Models/Action/action.rb'
require_relative 'Models/Action/unknown_action.rb'
require_relative 'Models/Action/add_children.rb'
require_relative 'Models/Action/alert.rb'
require_relative 'Models/Action/confirm.rb'
require_relative 'Models/Action/send_request.rb'
require_relative 'Models/Action/condition.rb'



require_relative 'FileHandler/file_handler.rb'
require_relative 'Common/constants.rb'
require_relative 'Templates/template_helper.rb'

# This is the main class of Beagle Schema
class ModelGenerator
  
  # Initializer for ModelGenerator
  #
  # @param components [BaseComponent] array of base components that will be translated to other languages
  def initialize(components)
    @objectType = nil
    @erb = nil
    @writer = FileHandler.new
    @components = components
    @import_manager = Hash.new("")
    @enum_import_manager = Hash.new("")
    @c = Constants.new

    helper = TemplateHelper.new

    components.each do |clazz|
      component = clazz.new
      type = component.synthax_type
      if helper.is_enum(component)
        @enum_import_manager[type.name] = "#{type.package}.#{type.name}"
      else
        @import_manager[type.name] = "#{type.package}.#{type.name}"
      end
    end
  end
  
  # Array of BaseComponents
  # @return [Array<BaseComponent>]
  attr_accessor :objectType

  # @return [Hash]
  attr_accessor :import_manager

  # @return [Hash]
  attr_accessor :enum_import_manager

  # This method is used to trigger the logic for code generation inside the templates
  # @return [String] the result of this method return a string that will be saved in a file
  def to_s
    @erb.result(binding)
  end

  # Generates models for all the supported languages
  def generate
    generate_swift
    generate_kotlin
    generate_kotlin_backend
    generate_ts
  end

  # Generates models for kotlin
  def generate_kotlin
    helper = TemplateHelper.new
    @erb = ERB.new(File.read("#{@c.templates}kotlin.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      suffix = helper.is_enum(@objectType) ? ".kt" : "Schema.kt"
      @writer.write(@c.kotlin_path, @objectType.name + suffix, to_s)
    end
  end

  # Generates models for kotlin backend
  def generate_kotlin_backend
    helper = TemplateHelper.new
    @erb = ERB.new(File.read("#{@c.templates}kotlin_backend.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      if !helper.is_enum(@objectType)
        @writer.write(@c.kotlin_backend_path, @objectType.name + ".kt", to_s)
      end
    end
  end
  
  # Generates models for swift
  def generate_swift
    ready_to_prod = [
      Button.new.name, EdgeValue.new.name, Flex.new.name,
      Size.new.name, UnitType.new.name, UnitValue.new.name,
      Style.new.name, CornerRadius.new.name, ScrollAxis.new.name,
      TextInputType.new.name, ScrollView.new.name, ImageContentMode.new.name,
      TextInput.new.name, Action.new.name, UnknownAction.new.name,
      Alert.new.name, Confirm.new.name, SendRequest.new.name,
      LazyComponent.new.name, Condition.new.name, NavigationBar.new.name,
      SafeArea.new.name, Screen.new.name
    ]
    @erb = ERB.new(File.read("#{@c.templates}swift.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      path = @c.swift_path

      if ready_to_prod.include? @objectType.name
        path += "../../../../../iOS/Schema/Sources/CodeGeneration/BeagleSchemaGenerated/"
      end

      @writer.write(path, @objectType.name + ".swift", to_s)
    end
  end

  # Generates models for type script
  def generate_ts
    @erb = ERB.new(File.read("#{@c.templates}ts.erb"), nil, '-')
    for component in @components
      @objectType = component.new
      @writer.write(@c.ts_path, @objectType.name + ".ts", to_s)
    end
  end

end

if __FILE__ == $0

  components = [
    # Components
    Button,
    Text,
    TextInputType,
    ImageContentMode,
    TextInput,
    # Layout
    CornerRadius,
    EdgeValue,
    Flex,
    Size,
    Style,
    UnitValue,
    UnitType,
    # Accessibility
    Accessibility,
    # Far from being usable
    Container,
    Image,
    AnalyticsEvent,
    AnalyticsClick,
    AnalyticsScreen,
    SendRequest,
    # ServerDriven
    ScrollAxis,
    ScrollView,
    # Action
    Action,
    UnknownAction,
    AddChildren,
    Alert,
    Confirm,
    Condition,
    # Lazy
    LazyComponent,
    # Screen
    SafeArea,
    Screen,
    NavigationBar,
    NavigationBarItem
  ]
  
  generator = ModelGenerator.new(components)
  message = "models generated!"
  
  Dir.chdir(ARGV[0])
  
  case ARGV[1]
  when "swift"
    generator.generate_swift
    puts "Swift #{message}"
  when "ts"
    generator.generate_ts
    puts "Type Script #{message}"
  when "kotlin"
    generator.generate_kotlin
    puts "Kotlin #{message}"
    generator.generate_kotlin_backend
    puts "Kotlin Backend #{message}"
  when "all"
    generator.generate
    puts "All language #{message}"
  else
    "You gave me #{ARGV[0]} -- I have no idea what to do with that."
  end

end