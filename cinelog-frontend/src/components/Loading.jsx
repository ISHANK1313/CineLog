import React from 'react';
import { Film } from 'lucide-react';

const Loading = ({ fullScreen = false }) => {
  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-dark-900 flex items-center justify-center z-50">
        <div className="text-center">
          <Film className="w-16 h-16 text-primary-500 animate-bounce mx-auto mb-4" />
          <p className="text-xl text-gray-400">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center py-12">
      <Film className="w-12 h-12 text-primary-500 animate-bounce" />
    </div>
  );
};

export default Loading;