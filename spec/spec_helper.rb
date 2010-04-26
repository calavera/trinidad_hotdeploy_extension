require 'rubygems'
begin
  require 'spec'
rescue LoadError
  gem 'rspec'
  require 'spec'
end

require 'trinidad'
begin
  require 'trinidad/jars'
rescue LoadError
  gem 'trinidad_jars'
  require 'trinidad/jars'
end

$:.unshift(File.dirname(__FILE__) + '/../lib')

require 'java'
require 'trinidad_hotdeploy_extension'
require 'mocha'

Spec::Runner.configure do |config|
  config.mock_with :mocha
end
