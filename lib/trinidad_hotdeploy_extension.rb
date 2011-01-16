module Trinidad
  module Extensions
    require File.expand_path('../../trinidad-libs/trinidad-hotdeploy-extension.jar', __FILE__)

    module Hotdeploy
      VERSION = '0.4.0'
    end

    class HotdeployWebAppExtension < WebAppExtension
      def configure(tomcat, app_context)
        monitor_file = File.expand_path(monitor(app_context))
        delay = @options[:delay] || 1000

        listener = org.jruby.trinidad.HotDeployLifecycleListener.new(app_context, monitor_file, delay)
        app_context.addLifecycleListener(listener)
        listener
      end

      def monitor(app_context)
        @options[:monitor] ||= begin
          doc_base = app_context.doc_base
          doc_base = File.join(doc_base, 'tmp/restart.txt') if !(doc_base =~ /\.war$/)
          doc_base
        end
      end
    end

    class HotdeployOptionsExtension < OptionsExtension
      def configure(parser, default_options)
        default_options[:extensions] ||= {}
        default_options[:extensions][:hotdeploy] = {}
      end
    end
  end
end
