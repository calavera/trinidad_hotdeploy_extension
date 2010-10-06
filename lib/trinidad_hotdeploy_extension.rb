module Trinidad
  module Extensions
    require File.expand_path('../../trinidad-libs/trinidad-hotdeploy-extension.jar', __FILE__)

    module Hotdeploy
      VERSION = '0.3.1'
    end

    class HotdeployWebAppExtension < WebAppExtension
      def configure(tomcat, app_context)
        @options[:monitor] ||= File.join(app_context.doc_base, 'tmp/restart.txt')
        monitor = File.expand_path(@options[:monitor])
        delay = @options[:delay] || 1000

        listener = org.jruby.trinidad.HotDeployLifecycleListener.new(app_context, monitor, delay)
        app_context.addLifecycleListener(listener)
        listener
      end
    end

    class HotdeployOptionsExtension < OptionsExtension
      def configure(parser, default_options)
        default_options[:extensions] ||= {}
        default_options[:extensions][:hotdeploy] = []
      end
    end
  end
end
